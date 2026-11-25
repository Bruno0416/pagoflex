package com.pagoflex.app.features.cumplimiento.data.remote.dto.response

data class CumplimientoResponse(
    val desde: String,
    val hasta: String,
    val elegibles: Int,
    val pagadosATiempo: Int,
    val porcentaje: Double?,
    val categoria: String?
)
