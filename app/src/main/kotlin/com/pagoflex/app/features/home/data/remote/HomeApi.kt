package com.pagoflex.app.features.home.data.remote

import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.home.data.remote.dto.response.CumplimientoHomeResponse
import com.pagoflex.app.features.home.data.remote.dto.response.PerfilHomeResponse
import com.pagoflex.app.features.home.data.remote.dto.response.ResumenHomeResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("compromisos/me/resumen")
    suspend fun resumen(@Query("mes") mes: String): ResumenHomeResponse

    @GET("compromisos/me")
    suspend fun compromisos(): List<MiCompromisoResponse>

    @GET("usuarios/me")
    suspend fun perfil(): PerfilHomeResponse

    @GET("usuarios/me/avatar")
    suspend fun avatar(): ResponseBody

    @GET("cumplimiento/me")
    suspend fun cumplimiento(): CumplimientoHomeResponse
}
