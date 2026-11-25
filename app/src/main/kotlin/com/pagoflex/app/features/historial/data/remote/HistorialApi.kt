package com.pagoflex.app.features.historial.data.remote

import com.pagoflex.app.features.historial.data.remote.dto.response.PagoHistorialResponse
import com.pagoflex.app.features.historial.data.remote.dto.response.AnulacionHistorialResponse
import com.pagoflex.app.features.historial.data.remote.dto.response.ComprobanteHistorialResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HistorialApi {
    @GET("pagos/me")
    suspend fun pagos(): List<PagoHistorialResponse>

    @GET("anulaciones/me")
    suspend fun anulaciones(): List<AnulacionHistorialResponse>

    @GET("comprobantes/{numero}")
    suspend fun comprobante(@Path("numero") numero: String): ComprobanteHistorialResponse
}
