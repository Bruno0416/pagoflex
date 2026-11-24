package com.pagoflex.app.features.auth.presentation.model

enum class PasoRecuperacion {
    CORREO,
    CODIGO,
    CONTRASENA,
    EXITO
}

data class RecuperarContrasenaUiState(
    val paso: PasoRecuperacion = PasoRecuperacion.CORREO,
    val correo: String = "",
    val codigo: String = "",
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val cargando: Boolean = false,
    val recuperacionExitosa: Boolean = false,
    // errores
    val errorCorreo: Int? = null,
    val errorCodigo: Int? = null,
    val errorContrasena: Int? = null,
    val errorConfirmacion: Int? = null,
    val errorRecuperacion: Int? = null,
    val codigoReenviado: Boolean = false,
    val requiereNuevoCodigo: Boolean = false
)
