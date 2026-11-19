package com.pagoflex.app.features.reportes.data.remote

import android.content.Context
import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.core.data.remote.ApiCliente
import com.pagoflex.app.core.data.remote.ErrorApi
import com.pagoflex.app.core.data.remote.llamar
import com.pagoflex.app.core.data.remote.llamarONulo
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.core.rules.ReglasReporte
import com.pagoflex.app.features.cobranza.data.remote.CobranzaApi
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.reportes.data.ResultadoReporte
import com.pagoflex.app.features.reportes.presentation.model.CompromisoReportable
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

// reportes contra el backend: mismas respuestas que la versión con room
class ReportesRemoto(
    private val soporte: SoporteApi,
    private val cobranza: CobranzaApi,
) {

    companion object {
        // null si no hay sesión en el backend
        fun crear(context: Context): ReportesRemoto? {
            val soporte = ApiCliente.siEnLinea<SoporteApi>(context) ?: return null
            val cobranza = ApiCliente.siEnLinea<CobranzaApi>(context) ?: return null
            return ReportesRemoto(soporte, cobranza)
        }
    }

    // ---------- usuario final ----------

    suspend fun compromisosDelUsuario(): List<CompromisoReportable> =
        llamar { cobranza.misCompromisos() }.map {
            CompromisoReportable(
                folio = it.folio,
                empresa = it.empresa,
                concepto = it.concepto,
                textoCuota = textoCuota(it.numeroCuota, it.totalCuotas),
                total = it.total,
                fechaVencimiento = LocalDate.parse(it.fechaVencimiento),
                estado = EstadoCompromiso.valueOf(it.estado)
            )
        }

    suspend fun misReportes(hoy: LocalDate): List<ReporteItem> =
        llamar { soporte.misReportes() }.map { it.aModelo(hoy) }

    suspend fun reportar(folio: String, motivo: MotivoReporte, comentario: String): ResultadoReporte =
        try {
            val creado = llamar { soporte.reportar(ReportarBody(folio, motivo.name, comentario.trim().ifBlank { null })) }
            ResultadoReporte.Creado(creado.numero)
        } catch (e: ErrorApi) {
            // si ya hay uno abierto el backend lo nombra en el mensaje (HU-06 CA-2)
            val abierto = e.detalle?.let { Regex("REP-\\d+").find(it)?.value }
            when {
                e.codigo == 409 && abierto != null -> ResultadoReporte.YaExiste(abierto)
                e.codigo == 409 -> ResultadoReporte.Error(R.string.rep_error_no_necesita)
                e.codigo == 404 -> ResultadoReporte.Error(R.string.rep_error_no_encontrado)
                else -> ResultadoReporte.Error(R.string.rep_error_generico)
            }
        } catch (_: IOException) {
            ResultadoReporte.Error(R.string.api_error_conexion)
        }

    // ---------- agente ----------

    suspend fun bandeja(hoy: LocalDate): List<ReporteItem> =
        llamar { soporte.bandeja() }.map { it.aModelo(hoy) }

    suspend fun detalle(numero: String, hoy: LocalDate): ReporteItem? =
        llamarONulo { soporte.detalle(numero) }?.aModelo(hoy)

    @StringRes
    suspend fun tomar(numero: String): Int? = gestionar { soporte.tomar(numero) }

    @StringRes
    suspend fun resolver(numero: String, respuesta: String): Int? =
        gestionar { soporte.resolver(numero, ResolverBody(respuesta.trim())) }

    // ---------- helpers privados ----------

    // null si salió bien o el mensaje según lo que respondió el backend (RN-23)
    @StringRes
    private suspend fun gestionar(peticion: suspend () -> retrofit2.Response<ReporteDto>): Int? =
        try {
            llamar(peticion)
            null
        } catch (e: ErrorApi) {
            when {
                e.codigo == 404 -> R.string.rep_error_reporte_no_encontrado
                e.codigo == 403 -> R.string.per_otro_agente
                e.codigo == 409 && e.detalle?.contains("tomar", ignoreCase = true) == true -> R.string.rep_error_tomar_primero
                e.codigo == 409 -> R.string.rep_error_ya_tomado
                e.codigo == 400 -> R.string.rep_error_sin_respuesta
                else -> R.string.rep_error_generico
            }
        } catch (_: IOException) {
            R.string.api_error_conexion
        }

    private fun textoCuota(numero: Int?, total: Int?): String? =
        if (numero != null && total != null) "$numero/$total" else null

    private fun ReporteDto.aModelo(hoy: LocalDate): ReporteItem {
        val creado = LocalDateTime.parse(fechaCreacion)
        return ReporteItem(
            numero = numero,
            motivo = MotivoReporte.valueOf(motivo),
            comentario = comentario,
            fechaCreacion = creado,
            estado = EstadoReporte.valueOf(estado),
            agente = agente,
            fechaToma = fechaToma?.let { LocalDateTime.parse(it) },
            respuesta = respuesta,
            fechaResolucion = fechaResolucion?.let { LocalDateTime.parse(it) },
            folio = folio,
            concepto = cobro.concepto,
            textoCuota = textoCuota(cobro.numeroCuota, cobro.totalCuotas),
            total = cobro.total,
            fechaVencimiento = LocalDate.parse(cobro.fechaVencimiento),
            estadoCompromiso = cobro.estado?.let { EstadoCompromiso.valueOf(it) } ?: EstadoCompromiso.EN_REVISION,
            empresa = cobro.empresa,
            usuario = usuario.nombre,
            correo = usuario.correo,
            telefono = usuario.telefono,
            diasHabiles = ReglasReporte.diasHabilesEntre(creado.toLocalDate(), hoy),
            atrasado = atrasado
        )
    }
}
