package com.pagoflex.app.features.pago.data.remote.dto.response

data class CotizacionPagoResponse(
    val folio: String,
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Long,
    val recargo: Long,
    val total: Long,
    val fechaVencimiento: String,
    val fechaConsulta: String,
    val cotizacionId: String
)
