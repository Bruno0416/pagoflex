package com.pagoflex.app.features.cobranza.data

import androidx.annotation.StringRes
import androidx.room3.withWriteTransaction
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Anulacion
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.profiles.ControlAcceso
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.core.rules.Accion
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.core.rules.ReglasCompromiso
import com.pagoflex.app.core.rules.estadoCompromisoDesdeNombre
import com.pagoflex.app.core.rules.nombre
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.cobranza.presentation.model.AnulacionCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.CampoCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.CompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.model.FormularioCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.PagoCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.PersonaCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.ReporteDelCompromiso
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.LocalDate
import java.time.LocalDateTime

// ejecutivo en sesión con su empresa
data class EjecutivoSesion(
    val idUsuario: Int,
    val nombre: String,
    val idEmpresa: Int?,
    val empresa: String?,
)

// persona encontrada por rut al registrar
data class PersonaEncontrada(
    val nombre: String,
    val correo: String,
)

sealed class ResultadoRegistro {
    data class Registrado(val compromiso: Compromiso) : ResultadoRegistro()
    data class ConErrores(
        val errores: Map<CampoCompromiso, Int> = emptyMap(),
        @StringRes val errorGeneral: Int? = null,
    ) : ResultadoRegistro()
}

// gestión de compromisos del ejecutivo: cobranza (HU-12), registro y anulación (HU-11)
class CobranzaRepository(private val db: LocalDatabase) {

    private val acceso = ControlAcceso(db)

    // solo un ejecutivo activo entra a la cobranza de su empresa (RN-22)
    suspend fun obtenerEjecutivo(usuarioId: Int): EjecutivoSesion? {
        if (!acceso.puede(usuarioId, Accion.VER_COMPROMISOS_EMPRESA)) return null

        val usuario = db.usuarioDao().obtenerPorId(usuarioId) ?: return null
        val empresa = usuario.idEmpresa?.let { db.empresaClienteDao().obtenerPorId(it) }

        return EjecutivoSesion(
            idUsuario = usuario.idUsuario,
            nombre = usuario.nombres.substringBefore(" "),
            idEmpresa = usuario.idEmpresa,
            empresa = empresa?.nombreFantasia
        )
    }

    // ---------- consultas ----------

    suspend fun obtenerCompromisos(empresaId: Int, hoy: LocalDate): List<CompromisoEmpresa> =
        db.compromisoDao().obtenerDeEmpresa(empresaId).map { it.aModelo(hoy) }

    suspend fun obtenerDetalle(empresaId: Int, folio: String, hoy: LocalDate): CompromisoEmpresa? =
        db.compromisoDao().obtenerDeEmpresaPorFolio(empresaId, folio)?.aModelo(hoy)

    // reporte sin resolver del compromiso, solo lectura y solo de su empresa (RN-22)
    suspend fun obtenerReporteActivo(empresaId: Int, folio: String): ReporteDelCompromiso? {
        if (db.compromisoDao().obtenerDeEmpresaPorFolio(empresaId, folio) == null) return null

        val reporte = db.reporteDao()
            .obtenerActivoDeCompromiso(folio, EstadoReporte.RESUELTO.nombre) ?: return null

        return ReporteDelCompromiso(
            numero = reporte.numero,
            motivoRes = MotivoReporte.desdeNombre(reporte.motivo).labelRes,
            comentario = reporte.comentario,
            estadoRes = EstadoReporte.desdeNombre(reporte.estado).labelRes,
            fecha = reporte.fechaCreacion
        )
    }

    // solo personas con rol usuario final
    suspend fun buscarPersona(rut: String): PersonaEncontrada? {
        if (Validaciones.validarRut(rut) != null) return null

        val usuario = db.usuarioDao().obtenerPorRut(Validaciones.formatearRut(rut)) ?: return null
        if (!esUsuarioFinal(usuario)) return null

        return PersonaEncontrada("${usuario.nombres} ${usuario.apellidos}", usuario.correo)
    }

    // ---------- registro (HU-11, RN-20) ----------

    suspend fun registrar(
        formulario: FormularioCompromiso,
        ejecutivo: EjecutivoSesion,
        ahora: LocalDateTime,
    ): ResultadoRegistro {
        if (!acceso.puede(ejecutivo.idUsuario, Accion.REGISTRAR_COMPROMISO)) {
            return ResultadoRegistro.ConErrores(errorGeneral = R.string.per_sin_permiso)
        }

        val errores = mutableMapOf<CampoCompromiso, Int>()
        val hoy = ahora.toLocalDate()

        fun revisar(campo: CampoCompromiso, error: Int?) {
            if (error != null) errores[campo] = error
        }

        // 1. persona: buscar por rut y pedir datos básicos solo si el rut es válido y no existe
        val errorRut = Validaciones.validarRut(formulario.rut)
        revisar(CampoCompromiso.RUT, errorRut)

        val existente = if (errorRut == null) {
            db.usuarioDao().obtenerPorRut(Validaciones.formatearRut(formulario.rut))
        } else {
            null
        }
        if (existente != null && !esUsuarioFinal(existente)) {
            errores[CampoCompromiso.RUT] = R.string.val_rut_no_usuario_final
        }

        val personaNueva = errorRut == null && existente == null
        if (personaNueva) {
            revisar(CampoCompromiso.NOMBRES, Validaciones.validarObligatorio(formulario.nombres, R.string.val_nombres_obligatorios))
            revisar(CampoCompromiso.APELLIDOS, Validaciones.validarObligatorio(formulario.apellidos, R.string.val_apellidos_obligatorios))
            revisar(CampoCompromiso.CORREO, Validaciones.validarCorreo(formulario.correo))
            revisar(CampoCompromiso.TELEFONO, Validaciones.validarTelefono(formulario.telefono))
        }

        // 2. datos del compromiso
        revisar(CampoCompromiso.CONCEPTO, Validaciones.validarObligatorio(formulario.concepto, R.string.val_concepto_obligatorio))
        revisar(CampoCompromiso.MONTO, Validaciones.validarMonto(formulario.monto))
        revisar(CampoCompromiso.VENCIMIENTO, Validaciones.validarFechaVencimiento(formulario.vencimiento, hoy))
        revisar(CampoCompromiso.CUOTA, Validaciones.validarCuota(formulario.numeroCuota, formulario.totalCuotas))

        if (errores.isNotEmpty()) return ResultadoRegistro.ConErrores(errores)

        val idEmpresa = ejecutivo.idEmpresa
            ?: return ResultadoRegistro.ConErrores(errorGeneral = R.string.cob_error_sin_empresa)
        val numeroCuota = formulario.numeroCuota.trim().toIntOrNull()

        // 3. no registrar dos veces el mismo compromiso
        if (existente != null) {
            val duplicados = db.compromisoDao().contarDuplicados(
                existente.idUsuario, idEmpresa, formulario.concepto, numeroCuota
            )
            if (duplicados > 0) {
                return ResultadoRegistro.ConErrores(errorGeneral = R.string.cob_error_duplicado)
            }
        }

        // 4. guardar persona (si es nueva) y compromiso en una sola transacción
        val compromiso = db.withWriteTransaction {
            val idUsuarioFinal = existente?.idUsuario ?: insertarPersona(formulario, ahora)
            val prefijo = "CP-${hoy.year}-"

            val nuevo = Compromiso(
                idCompromiso = 0,
                folio = ReglasCompromiso.siguienteFolio(db.compromisoDao().obtenerFolios(prefijo), hoy.year),
                idUsuarioFinal = idUsuarioFinal,
                idEmpresa = idEmpresa,
                concepto = formulario.concepto.trim(),
                numeroCuota = numeroCuota,
                totalCuotas = formulario.totalCuotas.trim().toIntOrNull(),
                monto = Validaciones.parsearMonto(formulario.monto)!!.toInt(),
                fechaVencimiento = Validaciones.parsearFecha(formulario.vencimiento)!!,
                idEstadoCompromiso = idEstado(EstadoCompromiso.PENDIENTE),
                recargoAplicado = 0,
                fechaCambioEstado = ahora,
                idEjecutivoRegistro = ejecutivo.idUsuario,
                fechaRegistro = ahora
            )
            val idCompromiso = db.compromisoDao().insertarCompromiso(nuevo).toInt()
            nuevo.copy(idCompromiso = idCompromiso)
        }

        return ResultadoRegistro.Registrado(compromiso)
    }

    // ---------- anulación (HU-11, RN-21) ----------

    // devuelve el id del mensaje de error o null si se anuló
    @StringRes
    suspend fun anular(
        folio: String,
        ejecutivo: EjecutivoSesion,
        motivo: MotivoAnulacion?,
        comentario: String,
        ahora: LocalDateTime,
    ): Int? {
        if (!acceso.puede(ejecutivo.idUsuario, Accion.ANULAR_COMPROMISO)) return R.string.per_sin_permiso

        // 1. buscar el compromiso y su estado actual
        val compromiso = db.compromisoDao().obtenerPorFolio(folio) ?: return R.string.cob_error_no_encontrado
        val estado = estadoActual(compromiso, ahora.toLocalDate())

        // 2. validar reglas de anulación
        ReglasCompromiso.validarAnulacion(
            estado, compromiso.idEmpresa, ejecutivo.idEmpresa, motivo, comentario
        )?.let { return it }

        // 3. guardar quién, por qué y cuándo, y dejar el compromiso anulado
        db.withWriteTransaction {
            db.anulacionDao().insertarAnulacion(
                Anulacion(
                    idCompromiso = compromiso.idCompromiso,
                    idEjecutivo = ejecutivo.idUsuario,
                    idMotivoAnulacion = requireNotNull(db.referenciasDao().obtenerIdMotivoAnulacion(motivo!!.nombre)),
                    comentario = comentario.trim().ifBlank { null },
                    fecha = ahora
                )
            )
            db.compromisoDao().actualizarEstado(
                compromiso.idCompromiso, idEstado(EstadoCompromiso.ANULADO), compromiso.recargoAplicado, ahora
            )
        }
        return null
    }

    // ---------- helpers privados ----------

    private suspend fun esUsuarioFinal(usuario: Usuario): Boolean =
        usuario.idRol == db.referenciasDao().obtenerIdRolPorCodigo(PerfilUsuario.USUARIO_FINAL.name)

    private suspend fun idEstado(estado: EstadoCompromiso): Int =
        requireNotNull(db.referenciasDao().obtenerIdEstadoCompromiso(estado.nombre)) {
            "No existe el estado ${estado.nombre}"
        }

    private suspend fun estadoActual(compromiso: Compromiso, hoy: LocalDate): EstadoCompromiso {
        val nombreEstado = requireNotNull(db.referenciasDao().obtenerNombreEstadoCompromiso(compromiso.idEstadoCompromiso))
        return ReglasCompromiso.estadoEfectivo(
            estadoCompromisoDesdeNombre(nombreEstado), compromiso.fechaVencimiento, hoy
        )
    }

    private suspend fun insertarPersona(formulario: FormularioCompromiso, ahora: LocalDateTime): Int {
        val idRol = requireNotNull(db.referenciasDao().obtenerIdRolPorCodigo(PerfilUsuario.USUARIO_FINAL.name))
        return db.usuarioDao().insertarUsuario(
            Usuario(
                idRol = idRol,
                rut = Validaciones.formatearRut(formulario.rut),
                nombres = formulario.nombres.trim(),
                apellidos = formulario.apellidos.trim(),
                correo = formulario.correo.trim().lowercase(),
                telefono = formulario.telefono.trim(),
                fechaCreacion = ahora,
                fechaIncorporacion = ahora.toLocalDate()
            )
        ).toInt()
    }

    private fun CompromisoEmpresaRow.aModelo(hoy: LocalDate): CompromisoEmpresa {
        // 1. reflejar vencimientos que aún no se guardaron
        val estado = ReglasCompromiso.estadoEfectivo(estadoCompromisoDesdeNombre(estado), fechaVencimiento, hoy)

        return CompromisoEmpresa(
            folio = folio,
            concepto = concepto,
            numeroCuota = numeroCuota,
            totalCuotas = totalCuotas,
            monto = monto.toLong(),
            // 2. recargo fijo de la empresa si quedó vencido
            recargo = ReglasCompromiso.recargoEfectivo(estado, recargoAplicado, recargoEmpresa).toLong(),
            fechaVencimiento = fechaVencimiento,
            estado = estado,
            diasAtraso = ReglasCompromiso.diasDeAtraso(fechaVencimiento, hoy),
            persona = PersonaCompromiso("$nombres $apellidos", rut, correo, telefono),
            pago = if (numeroComprobante != null && fechaPago != null && montoPagado != null) {
                PagoCompromiso(numeroComprobante, fechaPago, canal.orEmpty(), montoPagado.toLong())
            } else {
                null
            },
            registradoPor = registradoPor,
            fechaRegistro = fechaRegistro,
            anulacion = if (motivoAnulacion != null && fechaAnulacion != null) {
                AnulacionCompromiso(
                    MotivoAnulacion.desdeNombre(motivoAnulacion), comentarioAnulacion, fechaAnulacion, anuladoPor
                )
            } else {
                null
            }
        )
    }
}
