package com.pagoflex.app.features.perfil.data.remote.dto.request

data class ActualizarPerfilRequest(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val contrasenaActual: String? = null
)
