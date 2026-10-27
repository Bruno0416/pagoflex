package com.pagoflex.app.features.reconocimientos.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.features.reconocimientos.data.ReconocimientoObtenidoRow

data class ReconocimientosUiState(
    val cargando: Boolean = true,
    val reconocimientos: List<ReconocimientoObtenidoRow> = emptyList(),
    @StringRes val mensajeError: Int? = null
)
