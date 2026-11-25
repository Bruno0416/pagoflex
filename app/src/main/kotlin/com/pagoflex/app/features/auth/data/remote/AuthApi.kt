package com.pagoflex.app.features.auth.data.remote

import com.pagoflex.app.features.auth.data.remote.dto.request.LoginRequest
import com.pagoflex.app.features.auth.data.remote.dto.request.RecuperacionRequest
import com.pagoflex.app.features.auth.data.remote.dto.request.VerificarCodigoRequest
import com.pagoflex.app.features.auth.data.remote.dto.request.RecuperarContrasenaRequest
import com.pagoflex.app.features.auth.data.remote.dto.request.RegistrarRequest
import com.pagoflex.app.features.auth.data.remote.dto.response.AuthResponse
import com.pagoflex.app.features.auth.data.remote.dto.response.VerificarCodigoResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("X-Skip-Auth: true")
    @POST("auth/registro")
    suspend fun registrar(
        @Body request: RegistrarRequest
    )

    @Headers("X-Skip-Auth: true")
    @POST("auth/iniciar-sesion")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @Headers("X-Skip-Auth: true")
    @POST("auth/recuperacion")
    suspend fun solicitarRecuperacion(
        @Body request: RecuperacionRequest
    )

    @Headers("X-Skip-Auth: true")
    @POST("auth/recuperacion/reenviar")
    suspend fun reenviarCodigo(
        @Body request: RecuperacionRequest
    )

    @Headers("X-Skip-Auth: true")
    @POST("auth/recuperacion/verificar")
    suspend fun verificarCodigoRecuperacion(
        @Body request: VerificarCodigoRequest
    ): VerificarCodigoResponse

    @Headers("X-Skip-Auth: true")
    @POST("auth/recuperacion/restablecer")
    suspend fun recuperar(
        @Body request: RecuperarContrasenaRequest
    )
}
