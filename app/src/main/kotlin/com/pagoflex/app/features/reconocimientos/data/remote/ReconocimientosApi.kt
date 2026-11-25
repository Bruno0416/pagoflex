package com.pagoflex.app.features.reconocimientos.data.remote

import com.pagoflex.app.features.reconocimientos.data.remote.dto.response.ReconocimientoResponse
import retrofit2.http.GET

interface ReconocimientosApi {
    @GET("reconocimientos/me")
    suspend fun obtener(): List<ReconocimientoResponse>
}
