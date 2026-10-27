package com.pagoflex.app.features.reconocimientos.data

import java.time.LocalDate
import java.time.LocalDateTime

data class CompromisoPagoRow(
    val folio: String,
    val fechaVencimiento: LocalDate,
    val estado: String,
    val fechaPago: LocalDateTime?
)
