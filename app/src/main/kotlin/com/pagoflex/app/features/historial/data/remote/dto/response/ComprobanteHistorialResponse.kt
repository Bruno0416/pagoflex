package com.pagoflex.app.features.historial.data.remote.dto.response

data class ComprobanteHistorialResponse(
    val numero: String,
    val folio: String,
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val personaNombre: String,
    val personaRut: String,
    val fechaVencimiento: String,
    val monto: Long,
    val recargo: Long,
    val total: Long,
    val canal: String,
    val medio: String,
    val fechaPago: String
)
