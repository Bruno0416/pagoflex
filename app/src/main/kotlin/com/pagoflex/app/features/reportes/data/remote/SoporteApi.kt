package com.pagoflex.app.features.reportes.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// soporte-service a través del gateway; usuario y agente salen del token
interface SoporteApi {
    // ---------- usuario final ----------
    @POST("api/v1/reportes")
    suspend fun reportar(@Body body: ReportarBody): Response<ReporteDto>

    @GET("api/v1/reportes/me")
    suspend fun misReportes(
        @Query("page") pagina: Int = 0,
        @Query("size") tamano: Int = 50,
    ): Response<PaginaDto<ReporteDto>>

    // ---------- agente: abiertos y los suyos ----------
    @GET("api/v1/reportes")
    suspend fun bandeja(
        @Query("page") pagina: Int = 0,
        @Query("size") tamano: Int = 50,
    ): Response<PaginaDto<ReporteDto>>

    // ---------- ejecutivo: reporte vigente de un cobro de su empresa ----------
    @GET("api/v1/empresas/me/compromisos/{folio}/reporte-activo")
    suspend fun reporteActivo(@Path("folio") folio: String): Response<ReporteDto>

    @GET("api/v1/reportes/{numero}")
    suspend fun detalle(@Path("numero") numero: String): Response<ReporteDto>

    // abierto -> asignado
    @POST("api/v1/reportes/{numero}/asignaciones")
    suspend fun asignar(@Path("numero") numero: String): Response<ReporteDto>

    // asignado -> en atención
    @POST("api/v1/reportes/{numero}/atenciones")
    suspend fun atender(@Path("numero") numero: String): Response<ReporteDto>

    // en atención -> resuelto
    @POST("api/v1/reportes/{numero}/resoluciones")
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
