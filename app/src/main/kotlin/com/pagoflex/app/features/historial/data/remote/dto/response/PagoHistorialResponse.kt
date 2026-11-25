package com.pagoflex.app.features.historial.data.remote.dto.response

data class PagoHistorialResponse(
    val numeroComprobante: String,
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val montoPagado: Long,
    val fechaHora: String,
    val canal: String
)
