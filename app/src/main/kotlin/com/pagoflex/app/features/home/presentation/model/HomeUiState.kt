package com.pagoflex.app.features.home.presentation.model

import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import java.time.LocalDateTime
import java.time.YearMonth

data class HomeUiState(
    val nombreUsuario: String = "",
    val inicialesUsuario: String = "",
    val avatarUsuario: ByteArray? = null,
    val periodo: YearMonth? = null,
    val resumen: ResumenInicio = ResumenInicio(),
    val compromisos: List<MiCompromisoResponse> = emptyList(),
    val ultimaActualizacion: LocalDateTime? = null,

    val cargando: Boolean = false,
    val mensajeError: Int? = null
)
