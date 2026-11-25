package com.pagoflex.app.features.home.presentation.model

import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse

data class ListaCompromisosUiState(
    val compromisos: List<MiCompromisoResponse> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
