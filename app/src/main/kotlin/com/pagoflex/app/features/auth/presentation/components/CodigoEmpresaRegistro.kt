package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState

@Composable
fun CodigoEmpresaRegistro(
    uiState: RegisterUiState,
    setCodigoEmpresa: (String) -> Unit
) {
    TextField(
        label = stringResource(R.string.auth_api_codigo_empresa),
        value = uiState.codigoEmpresa,
        onValueChange = setCodigoEmpresa,
        placeholder = stringResource(R.string.auth_api_codigo_empresa_placeholder),
        error = uiState.errorCodigoEmpresa?.let { stringResource(it) },
        enabled = !uiState.cargando
    )
}
