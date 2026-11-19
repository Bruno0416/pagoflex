package com.pagoflex.app.features.cobranza.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// cobranza-service a través del gateway; el ejecutivo sale del token
interface CobranzaApi {
    // ---------- ejecutivo: su empresa ----------
    @GET("api/v1/empresas/me")
    suspend fun empresa(): Response<EmpresaDto>

    @GET("api/v1/empresas/me/compromisos")
    suspend fun compromisos(): Response<List<CompromisoDto>>

    @GET("api/v1/empresas/me/compromisos/{folio}")
    suspend fun detalle(@Path("folio") folio: String): Response<CompromisoDto>

    @GET("api/v1/empresas/me/personas/{rut}")
    suspend fun persona(@Path("rut") rut: String): Response<PersonaDto>

    @POST("api/v1/empresas/me/compromisos")
    suspend fun registrar(@Body body: RegistrarCompromisoBody): Response<CompromisoDto>

    @POST("api/v1/empresas/me/compromisos/{folio}/anulaciones")
    suspend fun anular(@Path("folio") folio: String, @Body body: AnularCompromisoBody): Response<CompromisoDto>

    // ---------- usuario final: sus compromisos de todas las empresas ----------
    @GET("api/v1/compromisos/me")
    suspend fun misCompromisos(): Response<List<MiCompromisoDto>>
}

// ---------- cuerpos ----------

data class RegistrarCompromisoBody(
    val rut: String,
    val nombres: String?,
    val apellidos: String?,
    val correo: String?,
    val telefono: String?,
    val concepto: String,
    val monto: Long,
    val fechaVencimiento: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
)

data class AnularCompromisoBody(val motivo: String, val comentario: String?)

// ---------- respuestas ----------

data class EmpresaDto(val codigo: String, val nombre: String)

data class PersonaDto(val nombre: String, val correo: String)

// fechas en texto iso tal como las manda el backend
data class CompromisoDto(
    val folio: String,
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Long,
    val recargo: Long,
    val total: Long,
    val fechaVencimiento: String,
    val estado: String,
    val diasAtraso: Long,
    val persona: PersonaCompromisoDto,
    val registradoPor: String?,
    val fechaRegistro: String,
    val anulacion: AnulacionDto?,
    val numeroComprobante: String? = null,
)

// en las listas el contacto viene vacío; solo el detalle lo trae
data class PersonaCompromisoDto(val nombre: String, val rut: String, val correo: String?, val telefono: String?)

data class MiCompromisoDto(
    val folio: String,
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val total: Long,
    val fechaVencimiento: String,
    val estado: String,
)

data class AnulacionDto(val motivo: String, val comentario: String?, val ejecutivo: String?, val fecha: String)
