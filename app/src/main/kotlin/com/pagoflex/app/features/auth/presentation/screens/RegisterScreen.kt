package com.pagoflex.app.features.auth.presentation.screens


import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.feedback.LoadingIndicator
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.selection.Checkbox
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.components.TextoEnlace
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState
import com.pagoflex.app.features.auth.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    volverAlLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterContent(
        uiState = uiState,
        setRut = viewModel::setRut,
        validarRutField = viewModel::validarRutField,
        setNombres = viewModel::setNombres,
        validarNombresField = viewModel::validarNombresField,
        setApellidos = viewModel::setApellidos,
        validarApellidosField = viewModel::validarApellidosField,
        setCorreo = viewModel::setCorreo,
        validarCorreoField = viewModel::validarCorreoField,
        setContrasena = viewModel::setContrasena,
        validarContrasenaField = viewModel::validarContrasenaField,
        setConfirmarContrasena = viewModel::setConfirmarContrasena,
        validarConfirmarContrasenaField = viewModel::validarConfirmarContrasenaField,
        setTerminosYCondiciones = viewModel::setTerminosYCondiciones,
        registrar = { viewModel.registrar(volverAlLogin) },
        volverAlLogin = volverAlLogin
    )
}


@Composable
private fun RegisterContent(
    uiState: RegisterUiState,
    setRut: (String) -> Unit,
    validarRutField: () -> Unit,
    setNombres: (String) -> Unit,
    validarNombresField: () -> Unit,
    setApellidos: (String) -> Unit,
    validarApellidosField: () -> Unit,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit,
    setContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    setConfirmarContrasena: (String) -> Unit,
    setTerminosYCondiciones: (Boolean) -> Unit,
    validarConfirmarContrasenaField: () -> Unit,

    registrar: () -> Unit,
    volverAlLogin: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Theme.dimens.paddingSides)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                Theme.dimens.spacingM
            )
        ) {
            Spacer(
                Modifier.size(Theme.dimens.spacingL)
            )

            Text(
                stringResource(
                    R.string.auth_registro_titulo
                ),
                style = Theme.type.headlineLarge
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingXS)
            )

            Text(
                stringResource(
                    R.string.auth_registro_descripcion
                ),
                style = Theme.type.bodyLarge
                    .copy(color = Theme.colores.onSurfaceVariant)
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingL)
            )

            // ----------- nombres y apellidos -----------

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


            // ----------- rut -----------

            TextField(
                label = stringResource(R.string.auth_registro_rut),
                value = uiState.rut,
                onValueChange = setRut,
                placeholder = stringResource(R.string.auth_registro_rut_placeholder),
                onUnfocus = validarRutField,
                error = uiState.errorRut?.let { stringResource(it) }
            )

            // ----------- correo -----------

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


            // ----------- contrasena -----------

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


            // ----------- confirmar contrasena -----------

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

            // ----------- términos y condiciones -----------
            Checkbox(
                uiState.terminosYCondiciones,
                setTerminosYCondiciones,
                label = stringResource(R.string.auth_registro_terminos_condiciones)
            )

            uiState.errorTerminosYCondiciones?.let {
                Text(stringResource(R.string.auth_error_terminos_y_condiciones))
            }

            // ----------- error formulario -----------

            uiState.errorRegistro?.let { errorRes ->
                Spacer(
                    Modifier.size(Theme.dimens.spacingM)
                )

                Text(
                    stringResource(errorRes),
                    style = Theme.type.labelLarge.copy(
                        color = Theme.colores.error,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Spacer(
                Modifier.size(Theme.dimens.spacingL)
            )

            // ----------- boton -----------
            Button(
                onClick = registrar,
                enabled = !uiState.cargando
            ) {
                if (uiState.cargando) {
                    LoadingIndicator()
                } else Text(stringResource(R.string.auth_registro_boton))
            }

            Spacer(
                Modifier.size(Theme.dimens.spacingXL)
            )

            TextoEnlace(
                texto = stringResource(R.string.auth_registro_ya_tengo_cuenta),
                textoEnlace = stringResource(R.string.auth_registro_iniciar_sesion),
                onClick = volverAlLogin
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingXL)
            )
        }
    }
}


// ----------------- PREVIEW -----------------
@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun PreviewRegisterContent() {
    PagoFlexTheme {
        RegisterContent(
            uiState = RegisterUiState(),
            setRut = {},
            validarRutField = {},
            setNombres = {},
            validarNombresField = {},
            setApellidos = {},
            validarApellidosField = {},
            setCorreo = {},
            validarCorreoField = {},
            setContrasena = {},
            validarContrasenaField = {},
            setConfirmarContrasena = {},
            validarConfirmarContrasenaField = {},
            setTerminosYCondiciones = {},
            registrar = {},
            volverAlLogin = {}
        )
    }
}
