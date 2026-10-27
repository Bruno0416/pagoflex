package com.pagoflex.app.features.auth.presentation.model

import com.pagoflex.app.core.profiles.model.SesionUsuario

data class AuthUiState(
    val cargando: Boolean = true,
    val sesion: SesionUsuario? = null,
    val error: Int? = null
)
