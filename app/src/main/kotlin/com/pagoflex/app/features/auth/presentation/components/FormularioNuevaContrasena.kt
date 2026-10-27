package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState

@Composable
fun FormularioNuevaContrasena(
    uiState: RecuperarContrasenaUiState,
    setNuevaContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    setConfirmarContrasena: (String) -> Unit,
    validarConfirmacionField: () -> Unit,
    restablecerContrasena: () -> Unit,
    cambiarDatos: () -> Unit
) {
    Text(
        stringResource(R.string.auth_recuperacion_pregunta_titulo),
        style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
    )

    TextField(
        label = stringResource(R.string.auth_recuperacion_nueva_contrasena),
        value = uiState.nuevaContrasena,
        onValueChange = setNuevaContrasena,
        onUnfocus = validarContrasenaField,
        isPassword = true,
        error = uiState.errorContrasena?.let { stringResource(it) }
    )

    TextField(
        label = stringResource(R.string.auth_registro_confirmar_contrasena),
        value = uiState.confirmarContrasena,
        onValueChange = setConfirmarContrasena,
        onUnfocus = validarConfirmacionField,
        isPassword = true,
        error = uiState.errorConfirmacion?.let { stringResource(it) }
    )

    uiState.errorRecuperacion?.let { errorRes ->
        Text(stringResource(errorRes), color = Theme.colores.error)
    }

    Button(onClick = restablecerContrasena, enabled = !uiState.cargando) {
        if (uiState.cargando) CircularProgressIndicator()
        else Text(stringResource(R.string.auth_recuperacion_guardar))
    }

    TextoEnlace(
        texto = "",
        textoEnlace = stringResource(R.string.auth_recuperacion_cambiar_correo),
        onClick = cambiarDatos
    )
}
