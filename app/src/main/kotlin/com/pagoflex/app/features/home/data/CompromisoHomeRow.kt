package com.pagoflex.app.features.home.data

import java.time.LocalDate

data class CompromisoHomeRow(
    val folio: String,
    val organizacion: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val fechaVencimiento: LocalDate,
    val monto: Int,
    val recargoAplicado: Int,
    val recargoEmpresa: Int,
    val estado: String
)
