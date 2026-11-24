package com.pagoflex.app.core.data.remote

import android.content.Context
import com.pagoflex.app.core.network.RetrofitClient
import retrofit2.Retrofit

// usa el RetrofitClient de bruno: misma sesión, token y renovación para todas las pantallas
object ApiCliente {

    fun retrofit(context: Context): Retrofit {
        RetrofitClient.initialize(context)
        return RetrofitClient.retrofit
    }

    inline fun <reified T> api(context: Context): T = retrofit(context).create(T::class.java)
}
