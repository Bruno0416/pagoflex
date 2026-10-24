package com.pagoflex.app.features.auth.presentation.model

import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion

data class RegisterUiState(
    val rut: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val confirmarContrasenaVisible: Boolean = false,
    val preguntas: List<PreguntaRecuperacion> = emptyList(),
    val idPreguntaRecuperacion: Int? = null,
    val respuestaRecuperacion: String = "",
    val terminosYCondiciones: Boolean = false,
    val cargando: Boolean = false,

    // errores
    val errorRut: Int? = null,
    val errorNombres: Int? = null,
    val errorApellidos: Int? = null,
    val errorCorreo: Int? = null,
    val errorContrasena: Int? = null,
    val errorConfirmarContrasena: Int? = null,
    val errorPreguntaRecuperacion: Int? = null,
    val errorRespuestaRecuperacion: Int? = null,
    val errorTerminosYCondiciones: Int? = null,
    val errorRegistro: Int? = null,
)
