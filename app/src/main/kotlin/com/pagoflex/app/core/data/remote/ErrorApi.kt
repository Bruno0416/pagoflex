package com.pagoflex.app.core.data.remote

import com.google.gson.JsonParser
import retrofit2.Response

// error del backend con su código http y el detalle del ProblemDetail
class ErrorApi(val codigo: Int, val detalle: String?) : Exception("HTTP $codigo: $detalle")

// devuelve el cuerpo o lanza ErrorApi; sin conexión sale la IOException de okhttp
suspend fun <T> llamar(peticion: suspend () -> Response<T>): T {
    val respuesta = peticion()
    if (respuesta.isSuccessful) {
        @Suppress("UNCHECKED_CAST")
        return respuesta.body() ?: Unit as T
    }
    throw ErrorApi(respuesta.code(), leerDetalle(respuesta.errorBody()?.string()))
}

// null si el backend respondió 404
suspend fun <T> llamarONulo(peticion: suspend () -> Response<T>): T? =
    try {
        llamar(peticion)
    } catch (e: ErrorApi) {
        if (e.codigo == 404) null else throw e
    }

private fun leerDetalle(cuerpo: String?): String? =
    try {
        cuerpo?.let { JsonParser.parseString(it).asJsonObject.get("detail")?.asString }
    } catch (_: Exception) {
        null
    }
