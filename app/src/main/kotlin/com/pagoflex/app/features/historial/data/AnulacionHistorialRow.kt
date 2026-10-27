package com.pagoflex.app.features.historial.data

import java.time.LocalDateTime

data class AnulacionHistorialRow(
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val fecha: LocalDateTime
)
