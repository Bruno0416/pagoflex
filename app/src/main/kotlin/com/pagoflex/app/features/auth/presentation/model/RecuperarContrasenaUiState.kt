package com.pagoflex.app.features.auth.presentation.model

import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val preguntas: List<PreguntaRecuperacion> = emptyList(),
    val idPreguntaRecuperacion: Int? = null,
    val respuesta: String = "",
    val identidadValidada: Boolean = false,
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val cargando: Boolean = false,
    val recuperacionExitosa: Boolean = false,
    // errores
    val errorCorreo: Int? = null,
    val errorPregunta: Int? = null,
    val errorRespuesta: Int? = null,
    val errorContrasena: Int? = null,
    val errorConfirmacion: Int? = null,
    val errorRecuperacion: Int? = null
)
