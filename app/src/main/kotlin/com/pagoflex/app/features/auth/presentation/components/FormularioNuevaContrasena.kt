package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState

@Composable
fun FormularioNuevaContrasena(
    uiState: RecuperarContrasenaUiState,
    setNuevaContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    setConfirmarContrasena: (String) -> Unit,
    validarConfirmacionField: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)) {
        Text(
            stringResource(R.string.auth_api_nueva_contrasena_descripcion),
            style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
        )
        TextField(
            label = stringResource(R.string.auth_recuperacion_nueva_contrasena),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null
                )
            },
            value = uiState.nuevaContrasena,
            onValueChange = setNuevaContrasena,
            onUnfocus = validarContrasenaField,
            isPassword = true,
            error = uiState.errorContrasena?.let { stringResource(it) }
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
            onUnfocus = validarConfirmacionField,
            isPassword = true,
            error = uiState.errorConfirmacion?.let { stringResource(it) }
        )
        RequisitosContrasena(uiState.nuevaContrasena)
        uiState.errorRecuperacion?.let {
            Text(stringResource(it), color = Theme.colores.error)
        }
    }
}
