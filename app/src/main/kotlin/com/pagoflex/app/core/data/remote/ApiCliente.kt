package com.pagoflex.app.core.data.remote

import android.content.Context
import com.pagoflex.app.BuildConfig
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// cliente único del gateway; cada petición lleva el token de la sesión
object ApiCliente {

    @Volatile
    private var retrofit: Retrofit? = null

    fun retrofit(context: Context): Retrofit =
        retrofit ?: synchronized(this) {
            retrofit ?: crear(SesionApi(context)).also { retrofit = it }
        }

    inline fun <reified T> api(context: Context): T = retrofit(context).create(T::class.java)

    private fun crear(sesion: SesionApi): Retrofit {
        // 1. cliente sin token solo para renovar la sesión
        val renovador = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(base().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)

        // 2. cliente principal con el token y la renovación al recibir 401
        val http = base()
            .addInterceptor { cadena ->
                val token = sesion.leerToken()
                val peticion = if (token != null) {
                    cadena.request().newBuilder().header("Authorization", "Bearer $token").build()
                } else {
                    cadena.request()
                }
                cadena.proceed(peticion)
            }
            .authenticator(renovarSesion(sesion, renovador))
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // el token de acceso dura poco: con el de renovación se pide otro y se repite la petición una vez
    private fun renovarSesion(sesion: SesionApi, auth: AuthApi) = Authenticator { _, respuesta ->
        if (respuesta.priorResponse != null) return@Authenticator null
        val renovacion = sesion.leerRenovacion() ?: return@Authenticator null
        val nuevos = try {
            auth.renovar(RenovarBody(renovacion)).execute().body()
        } catch (_: Exception) {
            null
        }
        if (nuevos == null) {
            sesion.borrar()
            return@Authenticator null
        }
        sesion.guardar(nuevos)
        respuesta.request.newBuilder().header("Authorization", "Bearer ${nuevos.tokenAcceso}").build()
    }

    // margen para emuladores lentos o señal débil, sin dejar la app pegada
    private fun base() = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
}
