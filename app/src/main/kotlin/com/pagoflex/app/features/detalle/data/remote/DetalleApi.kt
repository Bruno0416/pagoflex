package com.pagoflex.app.features.detalle.data.remote

import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DetalleApi {
    @GET("compromisos/me/{folio}")
    suspend fun detalle(@Path("folio") folio: String): MiCompromisoResponse
}
