package com.pagoflex.app.core.network.interceptors

import com.pagoflex.app.core.network.auth.SessionStore
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val store: SessionStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val publico = original.header(ENCABEZADO_PUBLICO) == "true"
        val builder = original.newBuilder().removeHeader(ENCABEZADO_PUBLICO)

        if (publico) {
            builder.tag(AuthRequestTag::class.java, AuthRequestTag(publico = true))
        } else {
            val actual = store.leerSesion()
                ?: throw IOException("No hay sesión autenticada")
            builder.header("Authorization", "Bearer ${actual.tokens.accessToken}")
            builder.tag(
                AuthRequestTag::class.java,
                AuthRequestTag(publico = false, sesionKey = actual.sesionKey)
            )
        }

        return chain.proceed(builder.build())
    }

    private companion object {
        const val ENCABEZADO_PUBLICO = "X-Skip-Auth"
    }
}
