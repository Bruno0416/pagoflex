package com.pagoflex.app.features.auth.data.remote.dto.request

data class RecuperarContrasenaRequest(
    val tokenRestablecimiento: String,
    val nuevaContrasena: String
)
