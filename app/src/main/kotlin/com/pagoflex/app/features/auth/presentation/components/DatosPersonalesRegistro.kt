package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState

@Composable
fun DatosPersonalesRegistro(
    uiState: RegisterUiState,
    setNombres: (String) -> Unit,
    validarNombresField: () -> Unit,
    setApellidos: (String) -> Unit,
    validarApellidosField: () -> Unit,
    setRut: (String) -> Unit,
    validarRutField: () -> Unit,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.auth_registro_nombres),
            value = uiState.nombres,
            onValueChange = setNombres,
            placeholder = stringResource(R.string.auth_registro_nombres_placeholder),
            onUnfocus = validarNombresField,
            error = uiState.errorNombres?.let { stringResource(it) }
        )

        TextField(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.auth_registro_apellidos),
            value = uiState.apellidos,
            onValueChange = setApellidos,
            placeholder = stringResource(R.string.auth_registro_apellidos_placeholder),
            onUnfocus = validarApellidosField,
            error = uiState.errorApellidos?.let { stringResource(it) }
        )
    }

    TextField(
        label = stringResource(R.string.auth_registro_rut),
        value = uiState.rut,
        onValueChange = setRut,
        placeholder = stringResource(R.string.auth_registro_rut_placeholder),
        onUnfocus = validarRutField,
        error = uiState.errorRut?.let { stringResource(it) }
    )

    TextField(
        label = stringResource(R.string.auth_registro_correo),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_mail),
                contentDescription = null
            )
        },
        value = uiState.correo,
        onValueChange = setCorreo,
        placeholder = stringResource(R.string.auth_registro_correo_placeholder),
        onUnfocus = validarCorreoField,
        error = uiState.errorCorreo?.let { stringResource(it) }
    )

}
