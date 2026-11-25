package com.pagoflex.app.features.auth.data

import org.json.JSONObject
import retrofit2.HttpException
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.network.auth.model.SesionUsuario
import com.pagoflex.app.core.network.auth.model.TokensSesion
import com.pagoflex.app.features.auth.data.remote.AuthApi
import com.pagoflex.app.features.auth.data.remote.dto.request.*
import com.pagoflex.app.features.auth.data.remote.dto.response.*
import retrofit2.create

class AuthRepository {

    private val api = RetrofitClient.retrofit.create<AuthApi>()

    suspend fun iniciarSesion(request: LoginRequest): AuthResponse {
        val response = api.login(request)

        RetrofitClient.sessionManager.iniciarSesion(
            usuario = SesionUsuario(perfil = response.rol),
            tokens = TokensSesion(
                accessToken = response.tokenAcceso,
                refreshToken = response.tokenRenovacion
            )
        )

        return response
    }

    fun camposInvalidos(error: HttpException): Map<String, String> {
        val contenido = error.response()?.errorBody()?.string().orEmpty()
        return runCatching {
            val campos = JSONObject(contenido).optJSONObject("invalid_fields")
                ?: return@runCatching emptyMap()
            buildMap {
                val claves = campos.keys()
                while (claves.hasNext()) {
                    val nombre = claves.next()
                    put(nombre, campos.optString(nombre))
                }
            }
        }.getOrDefault(emptyMap())
    }

    suspend fun registrarUsuario(request: RegistrarRequest) = api.registrar(request)

    suspend fun solicitarRecuperacion(request: RecuperacionRequest) =
        api.solicitarRecuperacion(request)

    suspend fun reenviarCodigo(request: RecuperacionRequest) =
        api.reenviarCodigo(request)

    suspend fun verificarCodigoRecuperacion(
        request: VerificarCodigoRequest
    ): VerificarCodigoResponse = api.verificarCodigoRecuperacion(request)

    suspend fun restablecerContrasena(request: RecuperarContrasenaRequest) =
        api.recuperar(request)
}
