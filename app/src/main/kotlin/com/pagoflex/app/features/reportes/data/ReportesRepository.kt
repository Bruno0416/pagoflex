package com.pagoflex.app.features.reportes.data

import androidx.annotation.StringRes
import androidx.room3.withWriteTransaction
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Reporte
import com.pagoflex.app.core.profiles.ControlAcceso
import com.pagoflex.app.core.rules.Accion
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.core.rules.ReglasCompromiso
import com.pagoflex.app.core.rules.ReglasReporte
import com.pagoflex.app.core.rules.estadoCompromisoDesdeNombre
import com.pagoflex.app.core.rules.nombre
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.reportes.presentation.model.CompromisoReportable
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import java.time.LocalDate
import java.time.LocalDateTime

sealed class ResultadoReporte {
    data class Creado(val numero: String) : ResultadoReporte()

    // si ya hay un reporte abierto se muestra ese (HU-06 CA-2)
    data class YaExiste(val numero: String) : ResultadoReporte()
    data class Error(@StringRes val mensaje: Int) : ResultadoReporte()
}

// reportes de problema: el usuario reporta (HU-06) y el agente gestiona (HU-10)
class ReportesRepository(private val db: LocalDatabase) {

    private val acceso = ControlAcceso(db)

    private val estadosVigentes = setOf(
        EstadoCompromiso.PENDIENTE,
        EstadoCompromiso.VENCIDO,
        EstadoCompromiso.EN_REVISION,
    )

    // ---------- usuario final ----------

    suspend fun obtenerCompromisosReportables(usuarioId: Int, hoy: LocalDate): List<CompromisoReportable> =
        obtenerCompromisosDeUsuario(usuarioId, hoy).filter { it.estado in estadosVigentes }

    suspend fun obtenerCompromisoDeUsuario(usuarioId: Int, folio: String, hoy: LocalDate): CompromisoReportable? =
        obtenerCompromisosDeUsuario(usuarioId, hoy).firstOrNull { it.folio == folio }

    suspend fun obtenerReportesDeUsuario(usuarioId: Int, hoy: LocalDate): List<ReporteItem> {
        if (!acceso.puede(usuarioId, Accion.VER_REPORTES_PROPIOS)) return emptyList()
        return db.reporteDao().obtenerDeUsuario(usuarioId).map { it.aModelo(hoy) }
    }

    suspend fun obtenerReporteActivo(folio: String, hoy: LocalDate): ReporteItem? =
        db.reporteDao().obtenerActivoDeCompromiso(folio, EstadoReporte.RESUELTO.nombre)?.aModelo(hoy)

    suspend fun reportar(
        folio: String,
        usuarioId: Int,
        motivo: MotivoReporte?,
        comentario: String,
        ahora: LocalDateTime,
    ): ResultadoReporte {
        if (!acceso.puede(usuarioId, Accion.REPORTAR_PROBLEMA)) return ResultadoReporte.Error(R.string.per_sin_permiso)

        // 1. el compromiso tiene que ser del usuario
        val compromiso = db.compromisoDao().obtenerPorFolio(folio)
        if (compromiso == null || compromiso.idUsuarioFinal != usuarioId) {
            return ResultadoReporte.Error(R.string.rep_error_no_encontrado)
        }

        // 2. si ya está en revisión no se crea otro reporte
        obtenerReporteActivo(folio, ahora.toLocalDate())?.let {
            return ResultadoReporte.YaExiste(it.numero)
        }

        // 3. validar estado, motivo y comentario
        val estado = estadoActual(compromiso.idEstadoCompromiso, compromiso.fechaVencimiento, ahora.toLocalDate())
        if (!ReglasReporte.puedeReportarse(estado)) return ResultadoReporte.Error(R.string.rep_error_no_necesita)
        if (motivo == null) return ResultadoReporte.Error(R.string.rep_error_elige_motivo)
        Validaciones.validarComentario(motivo == MotivoReporte.OTRO, comentario)?.let {
            return ResultadoReporte.Error(it)
        }

        // 4. si venció y el recargo aún no se guardó, se guarda antes de pasar a revisión (RN-03)
        val recargoEmpresa = db.empresaClienteDao().obtenerPorId(compromiso.idEmpresa)?.recargoAtraso ?: 0
        val recargo = ReglasCompromiso.recargoEfectivo(estado, compromiso.recargoAplicado, recargoEmpresa)

        // 5. guardar el reporte abierto y dejar el compromiso en revisión (RN-09)
        val numero = db.withWriteTransaction {
            val numero = ReglasReporte.siguienteNumero(db.reporteDao().obtenerNumeros())
            db.reporteDao().insertarReporte(
                Reporte(
                    idReporte = 0,
                    numero = numero,
                    idCompromiso = compromiso.idCompromiso,
                    idMotivoReporte = requireNotNull(db.referenciasDao().obtenerIdMotivoReporte(motivo.nombre)),
                    comentario = comentario.trim().ifBlank { null },
                    fechaCreacion = ahora,
                    idEstadoReporte = idEstadoReporte(EstadoReporte.ABIERTO)
                )
            )
            db.compromisoDao().actualizarEstado(
                compromiso.idCompromiso, idEstadoCompromiso(EstadoCompromiso.EN_REVISION),
                recargo, ahora
            )
            numero
        }
        return ResultadoReporte.Creado(numero)
    }

    // ---------- agente ----------

    suspend fun obtenerPrimerNombre(usuarioId: Int): String =
        db.usuarioDao().obtenerPorId(usuarioId)?.nombres?.substringBefore(" ").orEmpty()

    // solo el agente ve los reportes de todos los usuarios (RF-14)
    suspend fun obtenerTodos(agenteId: Int, hoy: LocalDate): List<ReporteItem> {
        if (!acceso.puede(agenteId, Accion.VER_TODOS_LOS_REPORTES)) return emptyList()
        return db.reporteDao().obtenerTodos().map { it.aModelo(hoy) }
    }

    suspend fun obtenerDetalle(numero: String, agenteId: Int, hoy: LocalDate): ReporteItem? {
        if (!acceso.puede(agenteId, Accion.VER_TODOS_LOS_REPORTES)) return null
        return db.reporteDao().obtenerPorNumero(numero)?.aModelo(hoy)
    }

    // queda registrado como responsable (RN-23)
    @StringRes
    suspend fun tomar(numero: String, agenteId: Int, ahora: LocalDateTime): Int? {
        if (!acceso.puede(agenteId, Accion.GESTIONAR_REPORTE)) return R.string.per_sin_permiso

        val reporte = db.reporteDao().obtenerPorNumero(numero) ?: return R.string.rep_error_reporte_no_encontrado
        if (!ReglasReporte.puedeTomarse(EstadoReporte.desdeNombre(reporte.estado))) {
            return R.string.rep_error_ya_tomado
        }

        db.reporteDao().tomar(reporte.idReporte, idEstadoReporte(EstadoReporte.EN_GESTION), agenteId, ahora)
        return null
    }

    // solo lo resuelve el agente responsable y con respuesta para el usuario (RN-23)
    @StringRes
    suspend fun resolver(numero: String, agenteId: Int, respuesta: String, ahora: LocalDateTime): Int? {
        if (!acceso.puede(agenteId, Accion.GESTIONAR_REPORTE)) return R.string.per_sin_permiso

        val reporte = db.reporteDao().obtenerPorNumero(numero) ?: return R.string.rep_error_reporte_no_encontrado
        if (!ReglasReporte.puedeResolverse(EstadoReporte.desdeNombre(reporte.estado))) {
            return R.string.rep_error_tomar_primero
        }
        if (reporte.idAgente != agenteId) return R.string.per_otro_agente
        if (respuesta.isBlank()) return R.string.rep_error_sin_respuesta

        db.withWriteTransaction {
            // 1. guardar la respuesta y dejar el reporte resuelto
            db.reporteDao().resolver(
                reporte.idReporte, idEstadoReporte(EstadoReporte.RESUELTO), respuesta.trim(), ahora
            )

            // 2. el compromiso sale de revisión: anulado o según su fecha (RN-09)
            val estadoActual = estadoCompromisoDesdeNombre(reporte.estadoCompromiso)
            val nuevoEstado = ReglasReporte.estadoAlResolver(estadoActual, reporte.fechaVencimiento, ahora.toLocalDate())
            if (nuevoEstado != estadoActual) {
                val recargo = ReglasCompromiso.recargoEfectivo(nuevoEstado, reporte.recargoAplicado, reporte.recargoEmpresa)
                db.compromisoDao().actualizarEstado(
                    reporte.idCompromiso, idEstadoCompromiso(nuevoEstado), recargo, ahora
                )
            }
        }
        return null
    }

    // ---------- helpers privados ----------

    private suspend fun obtenerCompromisosDeUsuario(usuarioId: Int, hoy: LocalDate): List<CompromisoReportable> =
        db.compromisoDao().obtenerParaHome(usuarioId).map { fila ->
            val estado = ReglasCompromiso.estadoEfectivo(
                estadoCompromisoDesdeNombre(fila.estado), fila.fechaVencimiento, hoy
            )
            val recargo = ReglasCompromiso.recargoEfectivo(estado, fila.recargoAplicado, fila.recargoEmpresa)

            CompromisoReportable(
                folio = fila.folio,
                empresa = fila.organizacion,
                concepto = fila.concepto,
                textoCuota = textoCuota(fila.numeroCuota, fila.totalCuotas),
                total = (fila.monto + recargo).toLong(),
                fechaVencimiento = fila.fechaVencimiento,
                estado = estado
            )
        }

    private suspend fun estadoActual(idEstado: Int, vencimiento: LocalDate, hoy: LocalDate): EstadoCompromiso {
        val nombre = requireNotNull(db.referenciasDao().obtenerNombreEstadoCompromiso(idEstado))
        return ReglasCompromiso.estadoEfectivo(estadoCompromisoDesdeNombre(nombre), vencimiento, hoy)
    }

    private suspend fun idEstadoReporte(estado: EstadoReporte): Int =
        requireNotNull(db.referenciasDao().obtenerIdEstadoReporte(estado.nombre)) {
            "No existe el estado de reporte ${estado.nombre}"
        }

    private suspend fun idEstadoCompromiso(estado: EstadoCompromiso): Int =
        requireNotNull(db.referenciasDao().obtenerIdEstadoCompromiso(estado.nombre)) {
            "No existe el estado ${estado.nombre}"
        }

    private fun textoCuota(numero: Int?, total: Int?): String? =
        if (numero != null && total != null) "$numero/$total" else null

    private fun ReporteRow.aModelo(hoy: LocalDate): ReporteItem {
        val estadoReporte = EstadoReporte.desdeNombre(estado)
        val estadoComp = ReglasCompromiso.estadoEfectivo(
            estadoCompromisoDesdeNombre(estadoCompromiso), fechaVencimiento, hoy
        )
        val recargo = ReglasCompromiso.recargoEfectivo(estadoComp, recargoAplicado, recargoEmpresa)

        return ReporteItem(
            numero = numero,
            motivo = MotivoReporte.desdeNombre(motivo),
            comentario = comentario,
            fechaCreacion = fechaCreacion,
            estado = estadoReporte,
            agente = agente,
            fechaToma = fechaToma,
            respuesta = respuesta,
            fechaResolucion = fechaResolucion,
            folio = folio,
            concepto = concepto,
            textoCuota = textoCuota(numeroCuota, totalCuotas),
            total = (monto + recargo).toLong(),
            fechaVencimiento = fechaVencimiento,
            estadoCompromiso = estadoComp,
            empresa = empresa,
            usuario = usuario,
            correo = correo,
            telefono = telefono,
            diasHabiles = ReglasReporte.diasHabilesEntre(fechaCreacion.toLocalDate(), hoy),
            atrasado = ReglasReporte.estaAtrasado(estadoReporte, fechaCreacion.toLocalDate(), hoy)
        )
    }
}
