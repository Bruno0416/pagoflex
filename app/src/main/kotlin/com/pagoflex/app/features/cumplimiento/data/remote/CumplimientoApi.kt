package com.pagoflex.app.features.cumplimiento.data.remote

import com.pagoflex.app.features.cumplimiento.data.remote.dto.response.CumplimientoResponse
import retrofit2.http.GET

interface CumplimientoApi {
    @GET("cumplimiento/me")
    suspend fun obtener(): CumplimientoResponse
}
