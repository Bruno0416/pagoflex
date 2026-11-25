package com.pagoflex.app.features.historial.presentation.model

import com.pagoflex.app.features.historial.data.remote.dto.response.ComprobanteHistorialResponse

data class ComprobanteUiState(
    val comprobante: ComprobanteHistorialResponse? = null,
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
