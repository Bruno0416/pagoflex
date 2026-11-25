package com.pagoflex.app.features.reportes.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

// api falsa de soporte-service: responde desde memoria para probar sin backend
class FakeSoporteApi : SoporteApi {

    var reportes = listOf(
        reporteDto("REP-0012", "ABIERTO", fecha = "2026-10-05T00:00:00"),
        reporteDto("REP-0009", "RESUELTO", fecha = "2026-09-18T00:00:00"),
    )

    // interruptores para simular respuestas del backend
    var sinConexion = false
    var codigoError = 0
    var detalleError = ""

    // lo último que recibió, para revisarlo en la prueba
    var ultimoReporte: ReportarBody? = null
    val pasos = mutableListOf<String>()

    override suspend fun reportar(body: ReportarBody): Response<ReporteDto> {
        ultimoReporte = body
        return responder(reporteDto("REP-0013", "ABIERTO", folio = body.folio, fecha = "2026-10-06T10:00:00"))
    }

    override suspend fun misReportes(pagina: Int, tamano: Int): Response<PaginaDto<ReporteDto>> =
        responder(pagina(reportes))

    override suspend fun bandeja(pagina: Int, tamano: Int): Response<PaginaDto<ReporteDto>> =
        responder(pagina(reportes))

    override suspend fun reporteActivo(folio: String): Response<ReporteDto> {
        val activo = reportes.firstOrNull { it.folio == folio && it.estado != "RESUELTO" } ?: return error(404)
        return responder(activo)
    }

    override suspend fun reportesEmpresa(pagina: Int, tamano: Int): Response<PaginaDto<ReporteDto>> = error(501)

    override suspend fun subirComprobante(numero: String, imagen: okhttp3.MultipartBody.Part): Response<ReporteDto> = error(501)

    override suspend fun comprobante(numero: String): Response<okhttp3.ResponseBody> = error(501)

    override suspend fun detalle(numero: String): Response<ReporteDto> {
        val encontrado = reportes.firstOrNull { it.numero == numero } ?: return error(404)
        return responder(encontrado)
    }

    override suspend fun asignar(numero: String): Response<ReporteDto> {
        pasos += "asignar"
        return responder(reportes.first().copy(estado = "ASIGNADO"))
    }

    override suspend fun atender(numero: String): Response<ReporteDto> {
        pasos += "atender"
        return responder(reportes.first().copy(estado = "EN_ATENCION"))
    }

    override suspend fun resolver(numero: String, body: ResolverBody): Response<ReporteDto> {
        pasos += "resolver"
        return responder(reportes.first().copy(estado = "RESUELTO"))
    }

    // ---------- helpers ----------

    private fun <T> pagina(lista: List<T>) = PaginaDto(lista, 0, 50, lista.size.toLong(), 1)

    private fun <T> responder(cuerpo: T): Response<T> {
        if (sinConexion) throw IOException("Servidor apagado")
        if (codigoError != 0) return error(codigoError)
        return Response.success(cuerpo)
    }

    private fun <T> error(codigo: Int): Response<T> =
        Response.error(codigo, """{"detail":"$detalleError"}""".toResponseBody("application/json".toMediaType()))

    companion object {
        fun reporteDto(numero: String, estado: String, folio: String = "CP-2026-0106", fecha: String) = ReporteDto(
            numero = numero,
            folio = folio,
            motivo = "MONTO_INCORRECTO",
            comentario = "No sé por qué me cobran \$318.500.",
            estado = estado,
            atrasado = false,
            fechaCreacion = fecha,
            cobro = CobroDto("Altos del Maipo", "Cuota pie departamento", 14, 36, 318_500, "2026-09-30", "EN_REVISION"),
            usuario = UsuarioReporteDto("Luis Alberto Muñoz Tapia", "lmunoz@correo.ejemplo", null),
            agente = null,
            fechaToma = null,
            respuesta = null,
            fechaResolucion = null,
        )
    }
}
