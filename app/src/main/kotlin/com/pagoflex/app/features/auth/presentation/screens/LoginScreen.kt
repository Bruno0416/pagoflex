package com.pagoflex.app.features.auth.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pagoflex.app.R
import com.pagoflex.app.core.profiles.model.SesionUsuario
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.components.Logo
import com.pagoflex.app.features.auth.presentation.components.TextoEnlace
import com.pagoflex.app.features.auth.presentation.model.LoginUiState
import com.pagoflex.app.features.auth.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    crearSesion: (SesionUsuario) -> Unit,
    irARegistro: () -> Unit,
    irARecuperar: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        setCorreo = viewModel::setCorreo,
        validarCorreoField = viewModel::validarCorreoField,
        setContrasena = viewModel::setContrasena,
        validarContrasenaField = viewModel::validarContrasenaField,
        iniciarSesion = { viewModel.iniciarSesion(crearSesion) },
        irARegistro = irARegistro,
        irARecuperar = irARecuperar,
    )
}


@Composable
private fun LoginContent(
    uiState: LoginUiState,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit,
    setContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    iniciarSesion: () -> Unit,
    irARegistro: () -> Unit,
    irARecuperar: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(Theme.dimens.paddingSides)
        ) {
            Spacer(
                Modifier.size(Theme.dimens.spacingXXL)
            )

            Logo()

            Spacer(
                Modifier.size(Theme.dimens.spacingXL)
            )

            Text(stringResource(R.string.auth_login_titulo), style = Theme.type.headlineLarge)

            Spacer(
                Modifier.size(Theme.dimens.spacingS)
            )

            Text(
                stringResource(R.string.auth_login_descripcion),
                style = Theme.type.bodyLarge
                    .copy(color = Theme.colores.onSurfaceVariant)
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingXXL)
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
                error = uiState.errorCorreo?.let { stringResource(it) }
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingM)
            )

            TextField(
                label = stringResource(R.string.auth_login_contrasena),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null
                    )
                },
                value = uiState.contrasena,
                onValueChange = setContrasena,
                placeholder = stringResource(R.string.auth_login_contrasena_placeholder),
                onUnfocus = validarContrasenaField,
                error = uiState.errorContrasena?.let { stringResource(it) },
                isPassword = true
            )

            uiState.errorLogin?.let { errorRes ->
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

            Spacer(Modifier.size(Theme.dimens.spacingM))

            TextoEnlace(
                texto = "",
                textoEnlace = stringResource(R.string.auth_login_recuperar_contrasena),
                onClick = irARecuperar
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingXXL)
            )

            Button(
                onClick = iniciarSesion,
                enabled = !uiState.cargando
            ) {
                if (uiState.cargando) {
                    CircularProgressIndicator( modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,)
                } else Text(stringResource(R.string.auth_login_boton))
            }

            Spacer(
                Modifier.size(Theme.dimens.spacingXXL)
            )

            TextoEnlace(
                texto = stringResource(R.string.auth_login_sin_cuenta),
                textoEnlace = stringResource(R.string.auth_login_crear_cuenta),
                onClick = irARegistro
            )

        }
    }
}


// ---------- preview ----------
@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun PreviewLoginContent() {
    PagoFlexTheme {
        LoginContent(
            uiState = LoginUiState(),
            setCorreo = {},
            validarCorreoField = {},
            setContrasena = {},
            validarContrasenaField = {},
            iniciarSesion = {},
            irARegistro = {},
            irARecuperar = {}
        )
    }
}
