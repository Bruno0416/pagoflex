package com.pagoflex.app.features.home.data.remote.dto.response

data class MiCompromisoResponse(
    val folio: String,
    val empresaCodigo: String?,
    val empresa: String,
    val correoAtencion: String?,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Long,
    val recargo: Long,
    val total: Long,
    val fechaVencimiento: String,
    val estado: String,
    val diasAtraso: Long,
    val pagable: Boolean,
    val folioAnterior: String?,
    val numeroComprobante: String?,
    val fechaPago: String?
)
