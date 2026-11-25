package com.pagoflex.app.features.auth.presentation.model

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val cargando: Boolean = false,

    val errorCorreo: Int? = null,
    val errorContrasena: Int? = null,
    val errorLogin: Int? = null,
)
