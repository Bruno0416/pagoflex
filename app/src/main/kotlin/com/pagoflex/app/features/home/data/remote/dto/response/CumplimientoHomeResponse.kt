package com.pagoflex.app.features.home.data.remote.dto.response

data class CumplimientoHomeResponse(
    val elegibles: Int,
    val pagadosATiempo: Int,
    val porcentaje: Double?,
    val categoria: String?
)
