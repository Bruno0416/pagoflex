package com.pagoflex.app.features.reportes.data.remote

import com.pagoflex.app.R
import com.pagoflex.app.core.network.RetrofitClient
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

// reportes contra el backend: mismas respuestas que la versión con room
class ReportesRemoto(
    private val soporte: SoporteApi,
    private val cobranza: CobranzaApi,
) {

    companion object {
        fun crear(): ReportesRemoto = ReportesRemoto(
            RetrofitClient.retrofit.create(SoporteApi::class.java),
            RetrofitClient.retrofit.create(CobranzaApi::class.java)
        )
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
        llamar { soporte.misReportes() }.contenido.map { it.aModelo(hoy) }

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

    // la foto ya viene en jpeg desde ComprobanteImagen
    suspend fun subirComprobante(numero: String, jpeg: ByteArray) {
        val parte = MultipartBody.Part.createFormData("file", "comprobante.jpg", jpeg.toRequestBody("image/jpeg".toMediaType()))
        llamar { soporte.subirComprobante(numero, parte) }
    }

    // ---------- agente ----------

    suspend fun bandeja(hoy: LocalDate): List<ReporteItem> =
        llamar { soporte.bandeja() }.contenido.map { it.aModelo(hoy) }

    suspend fun reportesEmpresa(hoy: LocalDate): List<ReporteItem> =
        llamar { soporte.reportesEmpresa() }.contenido.map { it.aModelo(hoy) }

    suspend fun detalle(numero: String, hoy: LocalDate): ReporteItem? =
        llamarONulo { soporte.detalle(numero) }?.aModelo(hoy)

    suspend fun comprobante(numero: String): ByteArray = llamar { soporte.comprobante(numero) }.bytes()

    // al tomarlo queda asignado y en atención; en la app se ve como en gestión
    suspend fun tomar(numero: String): Int? =
        gestionar { soporte.asignar(numero) } ?: gestionar { soporte.atender(numero) }

    // si quedó solo asignado se inicia la atención antes de resolver
    suspend fun resolver(numero: String, respuesta: String): Int? {
        val actual = try {
            llamarONulo { soporte.detalle(numero) }
        } catch (_: ErrorApi) {
            null
        } catch (_: IOException) {
            null
        }
        if (actual?.estado == "ASIGNADO") gestionar { soporte.atender(numero) }?.let { return it }
        return gestionar { soporte.resolver(numero, ResolverBody(respuesta.trim())) }
    }

    // ---------- helpers privados ----------

    // null si salió bien o el mensaje según lo que respondió el backend (RN-23)
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

    // asignado y en atención son los dos pasos de "en gestión" en la app
    private fun estadoApp(estado: String): EstadoReporte = when (estado) {
        "ASIGNADO", "EN_ATENCION" -> EstadoReporte.EN_GESTION
        else -> EstadoReporte.valueOf(estado)
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
            estado = estadoApp(estado),
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
            correo = usuario.correo.orEmpty(),
            telefono = usuario.telefono,
            diasHabiles = ReglasReporte.diasHabilesEntre(creado.toLocalDate(), hoy),
            atrasado = atrasado,
            tieneComprobante = tieneComprobante
        )
    }
}
