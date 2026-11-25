package com.pagoflex.app.features.reportes.data

import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.core.data.remote.Cuenta
import com.pagoflex.app.core.data.remote.CuentaRemota
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.reportes.data.remote.ReportesRemoto
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
// todo va a soporte-service; quién puede hacer qué lo decide el backend con el token
class ReportesRepository(
    private val remoto: ReportesRemoto,
    private val cuenta: CuentaRemota,
) {

    private val estadosVigentes = setOf(
        EstadoCompromiso.PENDIENTE,
        EstadoCompromiso.VENCIDO,
        EstadoCompromiso.EN_REVISION,
    )

    // ---------- usuario final ----------

    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerCompromisosReportables(usuarioId: Int, hoy: LocalDate): List<CompromisoReportable> =
        remoto.compromisosDelUsuario().filter { it.estado in estadosVigentes }

    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerCompromisoDeUsuario(usuarioId: Int, folio: String, hoy: LocalDate): CompromisoReportable? =
        remoto.compromisosDelUsuario().firstOrNull { it.folio == folio }

    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerReportesDeUsuario(usuarioId: Int, hoy: LocalDate): List<ReporteItem> =
        remoto.misReportes(hoy)

    suspend fun obtenerReporteActivo(folio: String, hoy: LocalDate): ReporteItem? =
        remoto.misReportes(hoy).firstOrNull { it.folio == folio && it.estado != EstadoReporte.RESUELTO }

    // el backend revisa que el cobro sea suyo y se pueda reportar; aquí motivo y comentario
    @Suppress("UNUSED_PARAMETER")
    suspend fun reportar(
        folio: String,
        usuarioId: Int,
        motivo: MotivoReporte?,
        comentario: String,
        ahora: LocalDateTime,
    ): ResultadoReporte {
        if (motivo == null) return ResultadoReporte.Error(R.string.rep_error_elige_motivo)
        Validaciones.validarComentario(motivo == MotivoReporte.OTRO, comentario)?.let {
            return ResultadoReporte.Error(it)
        }
        return remoto.reportar(folio, motivo, comentario)
    }

    // ---------- agente ----------

    // nombre del agente para la barra superior
    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerCabecera(usuarioId: Int): Cuenta? = cuenta.actual()

    // abiertos y los que tomó este agente (RF-14)
    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerTodos(agenteId: Int, hoy: LocalDate): List<ReporteItem> = remoto.bandeja(hoy)

    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerDetalle(numero: String, agenteId: Int, hoy: LocalDate): ReporteItem? =
        remoto.detalle(numero, hoy)

    // queda registrado como responsable (RN-23)
    @Suppress("UNUSED_PARAMETER")
    suspend fun tomar(numero: String, agenteId: Int, ahora: LocalDateTime): Int? = remoto.tomar(numero)

    // solo lo resuelve el agente responsable y con respuesta para el usuario (RN-23)
    @Suppress("UNUSED_PARAMETER")
    suspend fun resolver(numero: String, agenteId: Int, respuesta: String, ahora: LocalDateTime): Int? {
        if (respuesta.isBlank()) return R.string.rep_error_sin_respuesta
        return remoto.resolver(numero, respuesta)
    }
}
