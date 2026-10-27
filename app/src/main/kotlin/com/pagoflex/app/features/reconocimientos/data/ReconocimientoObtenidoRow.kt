package com.pagoflex.app.features.reconocimientos.data

import java.time.LocalDateTime

data class ReconocimientoObtenidoRow(
    val nombre: String,
    val descripcion: String,
    val fechaObtencion: LocalDateTime,
    val motivo: String
)
