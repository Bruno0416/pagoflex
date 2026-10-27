package com.pagoflex.app.features.cumplimiento.presentation.model

import com.pagoflex.app.features.cumplimiento.data.ResultadoCumplimiento

data class CumplimientoUiState(
    val resultado: ResultadoCumplimiento = ResultadoCumplimiento(),
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
