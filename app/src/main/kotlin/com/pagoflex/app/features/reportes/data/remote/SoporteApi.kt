package com.pagoflex.app.features.reportes.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// soporte-service a través del gateway; usuario y agente salen del token
interface SoporteApi {
    // ---------- usuario final ----------
    @POST("api/v1/soporte/reportes")
    suspend fun reportar(@Body body: ReportarBody): Response<ReporteDto>

    @GET("api/v1/soporte/reportes/mios")
    suspend fun misReportes(): Response<List<ReporteDto>>

    // ---------- agente ----------
    @GET("api/v1/soporte/bandeja")
    suspend fun bandeja(): Response<List<ReporteDto>>

    @GET("api/v1/soporte/bandeja/{numero}")
    suspend fun detalle(@Path("numero") numero: String): Response<ReporteDto>

    @POST("api/v1/soporte/bandeja/{numero}/tomar")
    suspend fun tomar(@Path("numero") numero: String): Response<ReporteDto>

    @POST("api/v1/soporte/bandeja/{numero}/resolver")
    suspend fun resolver(@Path("numero") numero: String, @Body body: ResolverBody): Response<ReporteDto>
}

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

data class UsuarioReporteDto(val nombre: String, val correo: String, val telefono: String?)
