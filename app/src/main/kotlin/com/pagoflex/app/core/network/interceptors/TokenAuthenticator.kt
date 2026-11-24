package com.pagoflex.app.core.network.interceptors

import com.pagoflex.app.core.network.auth.RefreshApi
import com.pagoflex.app.core.network.auth.RefreshRequest
import com.pagoflex.app.core.network.auth.SessionStore
import com.pagoflex.app.core.network.auth.model.TokensSesion
import java.io.IOException
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val store: SessionStore,
    private val refreshApi: RefreshApi
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401 || respuestas401(response) >= 2) return null

        val tag = response.request.tag(AuthRequestTag::class.java) ?: return null
        if (tag.publico || tag.sesionKey == null) return null

        val tokenEnviado = response.request.header("Authorization")
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")
            ?: return null

        synchronized(lock) {
            val actual = store.leerSesion() ?: return null
            if (actual.sesionKey != tag.sesionKey) return null

            if (actual.tokens.accessToken != tokenEnviado) {
                return solicitudConToken(response.request, actual.tokens.accessToken)
            }

            val resultado = try {
                refreshApi.refrescar(
                    RefreshRequest(actual.tokens.refreshToken)
                ).execute()
            } catch (_: IOException) {
                return null
            }

            if (!resultado.isSuccessful) {
                resultado.errorBody()?.close()
                if (resultado.code() == 401) {
                    store.borrarSesionSiCoincide(
                        actual.sesionKey,
                        actual.tokens.refreshToken
                    )
                }
                return null
            }

            val datos = resultado.body() ?: return null
            if (datos.tokenAcceso.isBlank()) return null

            val nuevosTokens = TokensSesion(
                accessToken = datos.tokenAcceso,
                refreshToken = datos.tokenRenovacion
                    ?.takeIf(String::isNotBlank)
                    ?: actual.tokens.refreshToken
            )

            val actualizado = try {
                store.actualizarTokensSiCoincide(
                    actual.sesionKey,
                    actual.tokens.refreshToken,
                    nuevosTokens
                )
            } catch (_: IllegalStateException) {
                false
            }

            if (!actualizado) return null
            return solicitudConToken(response.request, nuevosTokens.accessToken)
        }
    }

    private fun solicitudConToken(request: Request, token: String): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

    private fun respuestas401(response: Response): Int {
        var actual: Response? = response
        var cantidad = 0
        while (actual != null) {
            if (actual.code == 401) cantidad++
            actual = actual.priorResponse
        }
        return cantidad
    }
}
