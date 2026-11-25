package com.pagoflex.app.features.cumplimiento.presentation.model

import com.pagoflex.app.features.cumplimiento.data.remote.dto.response.CumplimientoResponse

data class CumplimientoUiState(
    val resultado: CumplimientoResponse? = null,
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
