package com.pagoflex.app.core.data.remote

import android.content.Context
import com.pagoflex.app.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// cliente único del gateway; cada petición lleva el token guardado si existe
object ApiCliente {

    @Volatile
    private var retrofit: Retrofit? = null

    fun retrofit(context: Context): Retrofit =
        retrofit ?: synchronized(this) {
            retrofit ?: crear(SesionApi(context)).also { retrofit = it }
        }

    // api del backend solo si hay sesión en línea, si no los repositorios usan room
    inline fun <reified T> siEnLinea(context: Context): T? =
        if (SesionApi(context).enLinea) retrofit(context).create(T::class.java) else null

    private fun crear(sesion: SesionApi): Retrofit {
        val http = OkHttpClient.Builder()
            // margen para emuladores lentos o señal débil, sin dejar la app pegada
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { cadena ->
                val token = sesion.leerToken()
                val peticion = if (token != null) {
                    cadena.request().newBuilder().header("Authorization", "Bearer $token").build()
                } else {
                    cadena.request()
                }
                cadena.proceed(peticion)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
