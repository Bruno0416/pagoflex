package com.pagoflex.app.features.perfil.data.remote.dto.response

data class PerfilResponse(
    val id: String? = null,
    val idPersona: String? = null,
    val rut: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val telefono: String? = null,
    val rol: String? = null,
    val urlAvatar: String? = null,
    val idEmpresa: String? = null
)
