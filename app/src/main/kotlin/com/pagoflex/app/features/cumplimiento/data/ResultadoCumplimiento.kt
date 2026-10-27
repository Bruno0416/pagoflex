package com.pagoflex.app.features.cumplimiento.data

enum class CategoriaCumplimiento {
    EJEMPLAR,
    AL_DIA,
    EN_CAMINO
}

data class ResultadoCumplimiento(
    val totalElegibles: Int = 0,
    val pagadosATiempo: Int = 0,
    val porcentaje: Double? = null,
    val categoria: CategoriaCumplimiento? = null
)
