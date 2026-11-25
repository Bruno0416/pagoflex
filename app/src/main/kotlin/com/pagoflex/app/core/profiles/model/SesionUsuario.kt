package com.pagoflex.app.core.profiles.model

import com.pagoflex.app.core.network.auth.model.PerfilUsuario

data class SesionUsuario(
    val usuarioId: Int,
    val perfil: PerfilUsuario
)
