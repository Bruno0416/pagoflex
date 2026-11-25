package com.pagoflex.app.features.historial.presentation.model

import com.pagoflex.app.features.historial.data.remote.dto.response.PagoHistorialResponse
import com.pagoflex.app.features.historial.data.remote.dto.response.AnulacionHistorialResponse

data class HistorialUiState(
    val comprobantes: List<PagoHistorialResponse> = listOf(),
    val anulaciones: List<AnulacionHistorialResponse> = listOf(),
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
