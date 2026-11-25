package com.pagoflex.app.features.auth.data.remote.dto.response

import com.pagoflex.app.core.network.auth.model.PerfilUsuario

data class AuthResponse(
    val tokenAcceso: String,
    val tokenRenovacion: String,
    val rol: PerfilUsuario
)
