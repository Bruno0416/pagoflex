package com.pagoflex.app.core.data.local

import com.pagoflex.app.core.data.local.entities.Anulacion
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.core.data.local.entities.Pago
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.core.validators.Validaciones
import java.time.LocalDate
import java.time.LocalDateTime

/*
    ---------- cobranza de septiembre de altos del maipo (HU-12) ----------
    el anexo 9 solo entrega los totales del mes: 120 compromisos, 2 anulados,
    104 pagados y $32.476.000 recaudados. se generan 119 compromisos sintéticos
    que junto con CP-2026-0106 cuadran con esos totales (tasa 88,1%).
*/
class CargaCobranzaSeptiembre(private val db: LocalDatabase) {

    private val vencimiento = LocalDate.of(2026, 9, 30)
    private val fechaCarga = LocalDateTime.of(2026, 8, 30, 9, 0)
    private val canales = listOf("App", "Portal de pagos", "Agente conversacional")

    suspend fun cargarDatos() {
        val referencias = db.referenciasDao()

        // 1. resolver ids de la empresa, el ejecutivo y los catálogos
        val idEmpresa = requireNotNull(db.empresaClienteDao().obtenerIdPorCodigo("EC-01"))
        val idEjecutivo = requireNotNull(db.usuarioDao().obtenerPorRut("14.629.853-2")).idUsuario
        val idRolUsuario = requireNotNull(referencias.obtenerIdRolPorCodigo(PerfilUsuario.USUARIO_FINAL.name))
        val idComuna = requireNotNull(referencias.obtenerIdComuna("Maipú"))
        val idPagado = requireNotNull(referencias.obtenerIdEstadoCompromiso("Pagado"))
        val idVencido = requireNotNull(referencias.obtenerIdEstadoCompromiso("Vencido"))
        val idAnulado = requireNotNull(referencias.obtenerIdEstadoCompromiso("Anulado"))
        val idMotivoError = requireNotNull(referencias.obtenerIdMotivoAnulacion("Error de registro"))
        val idMedio = requireNotNull(referencias.obtenerIdMedioPago("Pago simulado"))

        repeat(119) { i ->
            // 2. crear una persona sintética con rut válido
            val cuerpo = (20_100_000 + (i + 1) * 137).toString()
            val numero = "%03d".format(i + 1)
            val idUsuario = db.usuarioDao().insertarUsuario(
                Usuario(
                    idRol = idRolUsuario,
                    rut = Validaciones.formatearRut(cuerpo + Validaciones.calcularDv(cuerpo)),
                    nombres = "Cliente",
                    apellidos = "Demo $numero",
                    correo = "cliente$numero@correo.ejemplo",
                    telefono = "+56 9 0000 %04d".format(1000 + i),
                    fechaCreacion = fechaCarga,
                    idComuna = idComuna,
                    fechaIncorporacion = fechaCarga.toLocalDate()
                )
            ).toInt()

            // 3. definir el estado: 96 a tiempo, 8 con atraso, 2 anulados y 13 vencidos
            val pagadoATiempo = i in 0..95
            val pagadoConAtraso = i in 96..103
            val anulado = i in 104..105
            val monto = if (pagadoConAtraso) 331_000 else 310_000
            val recargo = if (pagadoConAtraso || i >= 106) 8_500 else 0
            val idEstado = when {
                pagadoATiempo || pagadoConAtraso -> idPagado
                anulado -> idAnulado
                else -> idVencido
            }

            // 4. insertar el compromiso registrado por la ejecutiva
            val idCompromiso = db.compromisoDao().insertarCompromiso(
                Compromiso(
                    idCompromiso = 0,
                    folio = "CP-2026-%04d".format(201 + i),
                    idUsuarioFinal = idUsuario,
                    idEmpresa = idEmpresa,
                    concepto = "Cuota pie departamento",
                    numeroCuota = 12,
                    totalCuotas = 36,
                    monto = monto,
                    fechaVencimiento = vencimiento,
                    idEstadoCompromiso = idEstado,
                    recargoAplicado = recargo,
                    fechaCambioEstado = fechaCarga,
                    idEjecutivoRegistro = idEjecutivo,
                    fechaRegistro = fechaCarga
                )
            ).toInt()

            // 5. agregar el pago o la anulación según corresponda
            if (pagadoATiempo || pagadoConAtraso) {
                val fechaPago = if (pagadoATiempo) {
                    LocalDateTime.of(2026, 9, 20 + i % 10, 10, 0)
                } else {
                    LocalDateTime.of(2026, 10, 2, 12, 0)
                }
                db.pagoDao().insertarPago(
                    Pago(
                        idPago = 0,
                        numeroComprobante = "CPR-1%05d".format(i),
                        idCompromiso = idCompromiso,
                        fechaHora = fechaPago,
                        montoPagado = monto + recargo,
                        idCanal = requireNotNull(referencias.obtenerIdCanal(canales[i % 3])),
                        idMedioPago = idMedio
                    )
                )
            }
            if (anulado) {
                db.anulacionDao().insertarAnulacion(
                    Anulacion(
                        idCompromiso = idCompromiso,
                        idEjecutivo = idEjecutivo,
                        idMotivoAnulacion = idMotivoError,
                        fecha = LocalDateTime.of(2026, 9, 15, 16, 0)
                    )
                )
            }
        }
    }
}
