package com.pagoflex.app.features.auth.data.remote.dto.request

data class RegistrarRequest(
    val correo: String,
    val contrasena: String,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val codigoEmpresa: String? = null
)
