package com.pagoflex.app.core.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// login de usuarios-service a través del gateway
interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginBody): Response<TokenRespuesta>
}

data class LoginBody(val email: String, val password: String)

data class TokenRespuesta(val token: String)
