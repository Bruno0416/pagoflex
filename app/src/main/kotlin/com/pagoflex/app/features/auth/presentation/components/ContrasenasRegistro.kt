package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState

@Composable
fun ContrasenasRegistro(
    uiState: RegisterUiState,
    setContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    setConfirmarContrasena: (String) -> Unit,
    validarConfirmarContrasenaField: () -> Unit
) {
    TextField(
        label = stringResource(R.string.auth_registro_contrasena),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null
            )
        },
        value = uiState.contrasena,
        onValueChange = setContrasena,
        placeholder = stringResource(R.string.auth_registro_contrasena_placeholder),
        onUnfocus = validarContrasenaField,
        error = uiState.errorContrasena?.let { stringResource(it) },
        isPassword = true
    )

    TextField(
        label = stringResource(R.string.auth_registro_confirmar_contrasena),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null
            )
        },
        value = uiState.confirmarContrasena,
        onValueChange = setConfirmarContrasena,
        placeholder = stringResource(R.string.auth_registro_confirmar_contrasena_placeholder),
        onUnfocus = validarConfirmarContrasenaField,
        error = uiState.errorConfirmarContrasena?.let { stringResource(it) },
        isPassword = true
    )

}
