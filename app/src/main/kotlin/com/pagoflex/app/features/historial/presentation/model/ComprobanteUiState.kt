package com.pagoflex.app.features.historial.presentation.model

import com.pagoflex.app.core.data.local.entities.Comprobante

data class ComprobanteUiState(
    val comprobante: Comprobante? = null,
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
