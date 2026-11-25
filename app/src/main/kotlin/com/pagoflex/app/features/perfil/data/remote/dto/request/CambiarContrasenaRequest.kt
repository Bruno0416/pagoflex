package com.pagoflex.app.features.perfil.data.remote.dto.request

data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val nuevaContrasena: String
)
