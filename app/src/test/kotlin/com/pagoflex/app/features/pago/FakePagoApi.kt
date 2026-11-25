package com.pagoflex.app.features.pago

import com.pagoflex.app.features.pago.data.remote.PagoApi
import com.pagoflex.app.features.pago.data.remote.dto.request.ConfirmarPagoRequest
import com.pagoflex.app.features.pago.data.remote.dto.response.CotizacionPagoResponse
import com.pagoflex.app.features.pago.data.remote.dto.response.PagoConfirmadoResponse

class FakePagoApi : PagoApi {
    val cotizacion = CotizacionPagoResponse(
        folio = "PF-001",
        empresa = "Empresa de prueba",
        concepto = "Cuota de noviembre",
        numeroCuota = 1,
        totalCuotas = 3,
        monto = 25000L,
        recargo = 0L,
        total = 25000L,
        fechaVencimiento = "2026-11-30",
        fechaConsulta = "2026-11-24",
        cotizacionId = "cotizacion-prueba"
    )

    override suspend fun cotizar(folio: String): CotizacionPagoResponse {
        return cotizacion.copy(folio = folio)
    }

    // Los tres tests elegidos no confirman el pago en el servidor.
    override suspend fun pagar(
        folio: String,
        idempotencyKey: String,
        body: ConfirmarPagoRequest
    ): PagoConfirmadoResponse = TODO()
}
