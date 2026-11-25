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
fun FormularioIdentidad(
    uiState: RecuperarContrasenaUiState,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)) {
        Text(
            stringResource(R.string.auth_api_recuperacion_correo_descripcion),
            style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
        )
        TextField(
            label = stringResource(R.string.auth_login_correo),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_mail),
                    contentDescription = null
                )
            },
            value = uiState.correo,
            onValueChange = setCorreo,
            placeholder = stringResource(R.string.auth_login_correo_placeholder),
            onUnfocus = validarCorreoField,
            error = uiState.errorCorreo?.let { stringResource(it) },
            enabled = !uiState.cargando
        )
        uiState.errorRecuperacion?.let {
            Text(stringResource(it), color = Theme.colores.error)
        }
    }
}
