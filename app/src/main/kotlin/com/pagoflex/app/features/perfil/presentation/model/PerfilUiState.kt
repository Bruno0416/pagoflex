package com.pagoflex.app.features.perfil.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.features.perfil.data.remote.dto.response.PerfilResponse

data class PerfilUiState(
    val perfil: PerfilResponse? = null,
    val avatar: ByteArray? = null,
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
    @StringRes val errorGeneral: Int? = null,
    val cambiandoContrasena: Boolean = false,
    @StringRes val errorNuevaContrasena: Int? = null,
    @StringRes val errorConfirmacion: Int? = null
) {
    val cambioCorreo: Boolean get() = perfil?.correo != correo.trim().lowercase()
}
