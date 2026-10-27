package com.pagoflex.app.features.pago.data

import androidx.room3.withWriteTransaction
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Pago
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.features.detalle.data.DetalleRepository
import java.time.LocalDate
import java.time.LocalDateTime

enum class MetodoPago(val nombreCatalogo: String) {
    DEBITO("Tarjeta de débito"),
    CREDITO("Tarjeta de crédito"),
    TRANSFERENCIA("Transferencia")
}

data class ResumenPago(
    val folio: String,
    val organizacion: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Int,
    val recargo: Int,
    val total: Long,
    val fechaVencimiento: LocalDate,
    val fechaConsulta: LocalDate
)

enum class MotivoRechazoPago {
    NO_DISPONIBLE, EN_REVISION, ANULADO, YA_PAGADO, ORDEN, MONTO_CAMBIO, MEDIO_INVALIDO
}

class PagoRechazado(
    val motivo: MotivoRechazoPago,
    val folioAnterior: String? = null
) : Exception()

/** Confirma un pago local; no se comunican datos con bancos o pasarelas. */
class PagoRepository(private val db: LocalDatabase) {
    private val detalleRepository = DetalleRepository(db)

    // ---------- preparacion del pago ----------
    suspend fun prepararPago(
        folio: String,
        usuarioId: Int,
        fechaActual: LocalDate = LocalDate.now()
    ): ResumenPago {
        // 1. validar la sesion del usuario
        if (db.usuarioDao().obtenerSesionUsuario(usuarioId)?.perfil != PerfilUsuario.USUARIO_FINAL) {
            throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)
        }
        // 2. consultar el compromiso y validar su estado
        val detalle = db.compromisoDao().obtenerDetalle(folio, usuarioId)
            ?: throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)
        validarEstado(detalle.estado)
        // 3. comprobar el orden de pago por empresa
        validarOrden(usuarioId, detalle.idEmpresa, detalle.fechaVencimiento, detalle.idCompromiso)

        // 4. impedir pagos duplicados
        if (db.pagoDao().obtenerNumeroPorCompromiso(detalle.idCompromiso) != null) {
            throw PagoRechazado(MotivoRechazoPago.YA_PAGADO)
        }

        // 5. calcular recargo y total a pagar
        val recargo = detalleRepository.calcularRecargo(detalle, fechaActual)
        val total = detalleRepository.calcularTotal(detalle, fechaActual)
        if (total <= 0 || total > Int.MAX_VALUE) {
            throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)
        }

        // 6. construir el resumen para confirmacion
        return ResumenPago(
            folio = detalle.folio,
            organizacion = detalle.organizacion,
            concepto = detalle.concepto,
            numeroCuota = detalle.numeroCuota,
            totalCuotas = detalle.totalCuotas,
            monto = detalle.monto,
            recargo = recargo,
            total = total,
            fechaVencimiento = detalle.fechaVencimiento,
            fechaConsulta = fechaActual
        )
    }

    // ---------- confirmacion del pago ----------
    suspend fun confirmarPago(
        usuarioId: Int,
        resumenAprobado: ResumenPago,
        metodo: MetodoPago
    ): String = db.withWriteTransaction {
        // 1. capturar una sola fecha y hora para la confirmacion
        val ahora = LocalDateTime.now()
        // 2. revalidar el compromiso y el total dentro de la transaccion
        val vigente = prepararPago(resumenAprobado.folio, usuarioId, ahora.toLocalDate())
        if (vigente != resumenAprobado) {
            throw PagoRechazado(MotivoRechazoPago.MONTO_CAMBIO)
        }
        val detalle = checkNotNull(db.compromisoDao().obtenerDetalle(vigente.folio, usuarioId))
        // 3. resolver los catalogos de estado canal y medio
        val referencias = db.referenciasDao()
        val estadoPagado = referencias.obtenerIdEstadoCompromiso("Pagado")
            ?: throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)
        val canalApp = referencias.obtenerIdCanal("App")
            ?: throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)
        val medioId = referencias.obtenerIdMedioPago(metodo.nombreCatalogo)
            ?: throw PagoRechazado(MotivoRechazoPago.MEDIO_INVALIDO)
        // 4. generar el siguiente numero de comprobante
        val siguienteNumero = (db.pagoDao().obtenerMayorCorrelativo() ?: 0) + 1
        val numero = "CPR-%06d".format(java.util.Locale.ROOT, siguienteNumero)

        // 5. actualizar el compromiso a pagado
        val actualizados = db.compromisoDao().marcarPagado(
            compromisoId = detalle.idCompromiso,
            usuarioId = usuarioId,
            estadoPagadoId = estadoPagado,
            recargo = vigente.recargo,
            fechaCambio = ahora
        )
        if (actualizados != 1) throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)

        // 6. registrar el pago y generar su comprobante
        db.pagoDao().procesarPago(
            Pago(
                idPago = 0,
                numeroComprobante = numero,
                idCompromiso = detalle.idCompromiso,
                fechaHora = ahora,
                montoPagado = vigente.total.toInt(),
                idCanal = canalApp,
                idMedioPago = medioId
            )
        )
        numero
    }

    // --- validar estado pagable ---
    private fun validarEstado(estado: String) {
        when (estado.trim().lowercase()) {
            "pendiente", "vencido" -> Unit
            "en revisión" -> throw PagoRechazado(MotivoRechazoPago.EN_REVISION)
            "anulado" -> throw PagoRechazado(MotivoRechazoPago.ANULADO)
            "pagado" -> throw PagoRechazado(MotivoRechazoPago.YA_PAGADO)
            else -> throw PagoRechazado(MotivoRechazoPago.NO_DISPONIBLE)
        }
    }

    // --- validar orden de vencimiento ---
    private suspend fun validarOrden(
        usuarioId: Int,
        empresaId: Int,
        vencimiento: LocalDate,
        compromisoId: Int
    ) {
        // 1. buscar compromisos anteriores de la misma empresa
        val anterior = db.compromisoDao().obtenerFolioAnteriorPagable(
            usuarioId, empresaId, vencimiento, compromisoId
        )
        // 2. bloquear si existe una obligacion anterior pagable
        if (anterior != null) throw PagoRechazado(MotivoRechazoPago.ORDEN, anterior)
    }
}
