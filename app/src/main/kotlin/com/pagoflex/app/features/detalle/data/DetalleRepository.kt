package com.pagoflex.app.features.detalle.data

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.detalle.data.remote.DetalleApi
import retrofit2.create

/** Consulta un compromiso del usuario autenticado; pagabilidad y total son autoritativos. */
class DetalleRepository {
    private val api by lazy { RetrofitClient.retrofit.create<DetalleApi>() }

    suspend fun obtenerDetalle(folio: String): MiCompromisoResponse = api.detalle(folio)
}
