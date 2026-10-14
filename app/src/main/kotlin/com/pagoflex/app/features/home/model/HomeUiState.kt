
package com.pagoflex.app.features.home.model

import java.time.LocalDateTime
import java.time.YearMonth

data class HomeUiState(
    val nombreUsuario: String = "",
    val periodo: YearMonth? = null,
    val resumen: ResumenInicio = ResumenInicio(),
    val compromisos: List<CompromisoInicio> = emptyList(),
    val ultimaActualizacion: LocalDateTime? = null,
    
    val cargando: Boolean = false,
    val mensajeError: String? = null
)
