package com.pagoflex.app.core.network

import android.content.Context
import com.pagoflex.app.core.network.auth.RefreshApi
import com.pagoflex.app.core.network.auth.SessionManager
import com.pagoflex.app.core.network.auth.SessionStore
import com.pagoflex.app.core.network.auth.storage.SharedPrefsSessionStore
import com.pagoflex.app.core.network.interceptors.AuthInterceptor
import com.pagoflex.app.core.network.interceptors.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:8080/api/v1/"

    @Volatile
    private var instancia: Retrofit? = null

    @Volatile
    private var almacen: SessionStore? = null

    @Volatile
    private var administrador: SessionManager? = null

    val retrofit: Retrofit
        get() = checkNotNull(instancia) {
            "RetrofitClient debe inicializarse antes de usar las APIs"
        }

    val sessionStore: SessionStore
        get() = checkNotNull(almacen) {
            "RetrofitClient debe inicializarse antes de usar la sesión"
        }

    val sessionManager: SessionManager
        get() = checkNotNull(administrador) {
            "RetrofitClient debe inicializarse antes de usar la sesión"
        }

    @Synchronized
    fun initialize(context: Context) {
        if (instancia != null) return

        val store = SharedPrefsSessionStore(context.applicationContext)

        val clienteRefresh = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val apiRefresh = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clienteRefresh)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshApi::class.java)

        val clientePrincipal = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(store))
            .authenticator(TokenAuthenticator(store, apiRefresh))
            .build()

        val retrofitPrincipal = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientePrincipal)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        almacen = store
        administrador = SessionManager(store)
        instancia = retrofitPrincipal
    }
}
