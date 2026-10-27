package com.pagoflex.app.features.perfil.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.core.data.local.dao.PerfilUsuarioRow

data class PerfilUiState(
    val perfil: PerfilUsuarioRow? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val editando: Boolean = false,
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    @StringRes val errorNombres: Int? = null,
    @StringRes val errorApellidos: Int? = null,
    @StringRes val errorCorreo: Int? = null,
    @StringRes val errorContrasena: Int? = null,
    @StringRes val mensaje: Int? = null,
    @StringRes val errorGeneral: Int? = null
) {
    val cambioCorreo: Boolean get() = perfil?.correo != correo.trim().lowercase()
}
