package com.pagoflex.app.features.home.model

import java.time.LocalDateTime

data class HomeUiState(
    val nombreUsuario: String = "",
    val resumen : ResumenInicio = ResumenInicio(),
    val compromisos: List<CompromisoInicio> = emptyList(),
    val ultimaActualizacion: LocalDateTime? = null,

    val cargando: Boolean = false,
    val sinConexion: Boolean = false,
    val mensajeError: String? = null
)
