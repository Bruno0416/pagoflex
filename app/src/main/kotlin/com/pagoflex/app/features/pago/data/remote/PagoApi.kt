package com.pagoflex.app.features.pago.data.remote

import com.pagoflex.app.features.pago.data.remote.dto.request.ConfirmarPagoRequest
import com.pagoflex.app.features.pago.data.remote.dto.response.CotizacionPagoResponse
import com.pagoflex.app.features.pago.data.remote.dto.response.PagoConfirmadoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PagoApi {
    @GET("compromisos/me/{folio}/cotizacion-pago")
    suspend fun cotizar(@Path("folio") folio: String): CotizacionPagoResponse

    @POST("compromisos/me/{folio}/pagos")
    suspend fun pagar(
        @Path("folio") folio: String,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body body: ConfirmarPagoRequest
    ): PagoConfirmadoResponse
}
