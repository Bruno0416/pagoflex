package com.pagoflex.app.features.historial.presentation.model

import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.features.historial.data.AnulacionHistorialRow

data class HistorialUiState(
    val comprobantes: List<Comprobante> = listOf(),
    val anulaciones: List<AnulacionHistorialRow> = listOf(),
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
