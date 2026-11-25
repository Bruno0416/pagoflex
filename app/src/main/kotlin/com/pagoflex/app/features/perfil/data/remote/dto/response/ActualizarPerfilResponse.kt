package com.pagoflex.app.features.perfil.data.remote.dto.response

import com.pagoflex.app.features.auth.data.remote.dto.response.AuthResponse

data class ActualizarPerfilResponse(
    val perfil: PerfilResponse,
    val sesion: AuthResponse?
)
