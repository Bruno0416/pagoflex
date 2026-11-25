package com.pagoflex.app.features.reportes.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// soporte-service a través del gateway; usuario y agente salen del token
interface SoporteApi {
    // ---------- usuario final ----------
    @POST("reportes")
    suspend fun reportar(@Body body: ReportarBody): Response<ReporteDto>

    @GET("reportes/me")
    suspend fun misReportes(
        @Query("page") pagina: Int = 0,
        @Query("size") tamano: Int = 50,
    ): Response<PaginaDto<ReporteDto>>

    // foto del comprobante de pago, igual que el avatar de perfil
    @Multipart
    @PUT("reportes/me/{numero}/comprobante")
    suspend fun subirComprobante(@Path("numero") numero: String, @Part imagen: MultipartBody.Part): Response<ReporteDto>

    // ---------- agente: abiertos y los suyos ----------
    @GET("reportes")
    suspend fun bandeja(
        @Query("page") pagina: Int = 0,
        @Query("size") tamano: Int = 50,
    ): Response<PaginaDto<ReporteDto>>

    @GET("empresas/me/reportes")
    suspend fun reportesEmpresa(
        @Query("page") pagina: Int = 0,
        @Query("size") tamano: Int = 50,
    ): Response<PaginaDto<ReporteDto>>

    // ---------- ejecutivo: reporte vigente de un cobro de su empresa ----------
    @GET("empresas/me/compromisos/{folio}/reporte-activo")
    suspend fun reporteActivo(@Path("folio") folio: String): Response<ReporteDto>

    @GET("reportes/{numero}")
    suspend fun detalle(@Path("numero") numero: String): Response<ReporteDto>

    @GET("reportes/{numero}/comprobante")
    suspend fun comprobante(@Path("numero") numero: String): Response<ResponseBody>

    // abierto -> asignado
    @POST("reportes/{numero}/asignaciones")
    suspend fun asignar(@Path("numero") numero: String): Response<ReporteDto>

    // asignado -> en atención
    @POST("reportes/{numero}/atenciones")
    suspend fun atender(@Path("numero") numero: String): Response<ReporteDto>

    // en atención -> resuelto
    @POST("reportes/{numero}/resoluciones")
    suspend fun resolver(@Path("numero") numero: String, @Body body: ResolverBody): Response<ReporteDto>
}

// una página de resultados del backend
data class PaginaDto<T>(
    val contenido: List<T>,
    val pagina: Int,
    val tamano: Int,
    val totalElementos: Long,
    val totalPaginas: Int,
)

data class ReportarBody(val folio: String, val motivo: String, val comentario: String?)

data class ResolverBody(val respuesta: String)

// fechas en texto iso tal como las manda el backend
data class ReporteDto(
    val numero: String,
    val folio: String,
    val motivo: String,
    val comentario: String?,
    val estado: String,
    val atrasado: Boolean,
    val fechaCreacion: String,
    val cobro: CobroDto,
    val usuario: UsuarioReporteDto,
    val agente: String?,
    val fechaToma: String?,
    val fechaAtencion: String? = null,
    val respuesta: String?,
    val fechaResolucion: String?,
    val tieneComprobante: Boolean = false,
)

data class CobroDto(
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val total: Long,
    val fechaVencimiento: String,
    val estado: String?,
)

data class UsuarioReporteDto(val nombre: String, val correo: String?, val telefono: String?)
