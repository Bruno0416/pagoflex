package com.pagoflex.app.features.cumplimiento.data

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.cumplimiento.data.remote.CumplimientoApi
import com.pagoflex.app.features.cumplimiento.data.remote.dto.response.CumplimientoResponse
import retrofit2.create

class CumplimientoRepository {
    private val api = RetrofitClient.retrofit.create<CumplimientoApi>()

    suspend fun obtenerCumplimiento(): CumplimientoResponse = api.obtener()
}
