package com.pagoflex.app.features.historial.presentation.model

import com.pagoflex.app.core.data.local.entities.Comprobante

data class HistorialUiState(
    val comprobantes: List<Comprobante> = listOf(),
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
