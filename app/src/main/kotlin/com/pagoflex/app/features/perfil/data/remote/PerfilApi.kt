package com.pagoflex.app.features.perfil.data.remote

import com.pagoflex.app.features.perfil.data.remote.dto.request.ActualizarPerfilRequest
import com.pagoflex.app.features.perfil.data.remote.dto.response.ActualizarPerfilResponse
import com.pagoflex.app.features.perfil.data.remote.dto.response.PerfilResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part

interface PerfilApi {
    @GET("usuarios/me")
    suspend fun obtenerPerfil(): PerfilResponse

    @PATCH("usuarios/me")
    suspend fun actualizar(@Body request: ActualizarPerfilRequest): ActualizarPerfilResponse

    @GET("usuarios/me/avatar")
    suspend fun descargarAvatar(): ResponseBody

    @Multipart
    @PUT("usuarios/me/avatar")
    suspend fun guardarAvatar(@Part imagen: MultipartBody.Part): PerfilResponse

    @DELETE("usuarios/me/avatar")
    suspend fun eliminarAvatar()
}
