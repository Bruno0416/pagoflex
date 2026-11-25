package com.pagoflex.app.features.mensajeria.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// mensajeria-service a través del gateway; quién escribe sale del token
// los eventos en vivo (sse) no pasan por retrofit, los lee MensajeriaRemoto con okhttp
interface MensajeriaApi {

    @GET("conversaciones/reportes/{numero}")
    suspend fun conversacion(@Path("numero") numero: String): Response<ConversacionDto>

    // del más antiguo al más nuevo; cursorAnterior sirve para pedir los anteriores
    @GET("conversaciones/reportes/{numero}/mensajes")
    suspend fun mensajes(
        @Path("numero") numero: String,
        @Query("cursor") cursor: Long? = null,
        @Query("limit") limite: Int = 30,
    ): Response<PaginaMensajesDto>

    // con el mismo clientMessageId el backend no lo duplica
    @POST("conversaciones/reportes/{numero}/mensajes")
    suspend fun enviar(@Path("numero") numero: String, @Body body: EnviarMensajeBody): Response<MensajeDto>

    @PUT("conversaciones/reportes/{numero}/lecturas")
    suspend fun marcarLeido(@Path("numero") numero: String, @Body body: LecturaBody): Response<Unit>
}

data class ConversacionDto(
    val reporteNumero: String,
    val estadoReporte: String,
    val soloLectura: Boolean,
    val ultimaSecuencia: Long,
    val ultimaSecuenciaLeida: Long,
    val fechaCreacion: String?,
)

data class PaginaMensajesDto(
    val mensajes: List<MensajeDto>,
    val cursorAnterior: Long?,
)

// fechas en texto iso tal como las manda el backend
data class MensajeDto(
    // id y autorId en texto: gson acepta número o uuid
    val id: String?,
    val reporteNumero: String,
    val autorId: String?,
    val autor: String?,
    val rol: String?,
    val texto: String,
    val enviadoEn: String?,
    val clientMessageId: String?,
    val secuencia: Long,
)

data class EnviarMensajeBody(val texto: String, val clientMessageId: String)

data class LecturaBody(val ultimaSecuencia: Long)
