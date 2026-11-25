package com.pagoflex.app.features.reconocimientos.data.remote.dto.response

data class ReconocimientoResponse(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val motivo: String,
    val fechaObtencion: String
)
