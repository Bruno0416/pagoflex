package com.pagoflex.app.features.mensajeria.presentation.model

import androidx.annotation.StringRes

data class ConversacionActiva(
    val numero: String,
    val folio: String,
    val concepto: String,
    val empresa: String,
    val usuario: String,
    val mensajes: Long,
    val sinLeer: Long,
)

data class BandejaMensajesUiState(
    val cargando: Boolean = true,
    val conversaciones: List<ConversacionActiva> = emptyList(),
    @StringRes val error: Int? = null,
)
