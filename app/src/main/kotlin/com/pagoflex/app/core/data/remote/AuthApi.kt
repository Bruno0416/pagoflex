package com.pagoflex.app.core.data.remote

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// sesión en usuarios-service a través del gateway
interface AuthApi {
    @POST("auth/iniciar-sesion")
    suspend fun iniciarSesion(@Body body: LoginBody): Response<TokensDto>

    // síncrona: la usa el renovador de okhttp cuando el token de acceso vence
    @POST("auth/renovar")
    fun renovar(@Body body: RenovarBody): Call<TokensDto>

    @GET("usuarios/me")
    suspend fun perfil(): Response<PerfilDto>
}

data class LoginBody(val correo: String, val contrasena: String)

data class RenovarBody(val tokenRenovacion: String)

data class TokensDto(
    val tokenAcceso: String,
    val tokenRenovacion: String,
    val tipoToken: String,
    val expiraEnSegundos: Long,
)

// perfil de la cuenta del token; idEmpresa solo en ejecutivos
data class PerfilDto(
    val nombres: String,
    val apellidos: String,
    val correo: String?,
    val rol: String,
    val urlAvatar: String?,
    val idEmpresa: String?,
)
