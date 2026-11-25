package com.pagoflex.app.features.historial.data

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.historial.data.remote.HistorialApi
import com.pagoflex.app.features.historial.data.remote.dto.response.AnulacionHistorialResponse
import com.pagoflex.app.features.historial.data.remote.dto.response.ComprobanteHistorialResponse
import com.pagoflex.app.features.historial.data.remote.dto.response.PagoHistorialResponse
import retrofit2.create

/** Consulta movimientos y comprobantes sin transformar ni duplicar los contratos del backend. */
class HistorialRepository {
    private val api = RetrofitClient.retrofit.create<HistorialApi>()

    suspend fun obtenerHistorial(): List<PagoHistorialResponse> = api.pagos()

    suspend fun obtenerAnulaciones(): List<AnulacionHistorialResponse> = api.anulaciones()

    suspend fun obtenerComprobante(numero: String): ComprobanteHistorialResponse = api.comprobante(numero)
}
