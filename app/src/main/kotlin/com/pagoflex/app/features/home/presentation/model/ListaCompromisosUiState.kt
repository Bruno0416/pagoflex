package com.pagoflex.app.features.home.presentation.model

data class ListaCompromisosUiState(
    val compromisos: List<CompromisoInicio> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: Int? = null
)
