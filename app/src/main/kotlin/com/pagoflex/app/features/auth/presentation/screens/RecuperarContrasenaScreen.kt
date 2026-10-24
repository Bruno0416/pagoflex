package com.pagoflex.app.features.auth.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.components.FormularioIdentidad
import com.pagoflex.app.features.auth.presentation.components.FormularioNuevaContrasena
import com.pagoflex.app.features.auth.presentation.components.TextoEnlace
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState
import com.pagoflex.app.features.auth.presentation.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    viewModel: RecuperarContrasenaViewModel,
    volverAlLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecuperarContrasenaContent(
        uiState = uiState,
        setCorreo = viewModel::setCorreo,
        validarCorreoField = viewModel::validarCorreoField,
        setPreguntaRecuperacion = viewModel::setPreguntaRecuperacion,
        setRespuesta = viewModel::setRespuesta,
        validarRespuestaField = viewModel::validarRespuestaField,
        validarIdentidad = viewModel::validarIdentidad,
        cambiarDatos = viewModel::cambiarDatos,
        setNuevaContrasena = viewModel::setNuevaContrasena,
        validarContrasenaField = viewModel::validarContrasenaField,
        setConfirmarContrasena = viewModel::setConfirmarContrasena,
        validarConfirmacionField = viewModel::validarConfirmacionField,
        restablecerContrasena = viewModel::restablecerContrasena,
        volverAlLogin = volverAlLogin
    )
}

@Composable
private fun RecuperarContrasenaContent(
    uiState: RecuperarContrasenaUiState,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit,
    setPreguntaRecuperacion: (Int) -> Unit,
    setRespuesta: (String) -> Unit,
    validarRespuestaField: () -> Unit,
    validarIdentidad: () -> Unit,
    cambiarDatos: () -> Unit,
    setNuevaContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    setConfirmarContrasena: (String) -> Unit,
    validarConfirmacionField: () -> Unit,
    restablecerContrasena: () -> Unit,
    volverAlLogin: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Theme.dimens.paddingSides)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            Spacer(Modifier.size(Theme.dimens.spacingL))

            Text(
                stringResource(R.string.auth_recuperacion_titulo),
                style = Theme.type.headlineLarge
            )

            if (uiState.recuperacionExitosa) {
                // ---------- recuperacion exitosa ----------
                Text(
                    stringResource(R.string.auth_recuperacion_exito),
                    style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
                )

                Button(onClick = volverAlLogin) {
                    Text(stringResource(R.string.auth_recuperacion_volver))
                }
            } else if (!uiState.identidadValidada) {
                FormularioIdentidad(
                    uiState, setCorreo, validarCorreoField,
                    setPreguntaRecuperacion, setRespuesta, validarRespuestaField,
                    validarIdentidad
                )
            } else {
                FormularioNuevaContrasena(
                    uiState, setNuevaContrasena, validarContrasenaField,
                    setConfirmarContrasena, validarConfirmacionField,
                    restablecerContrasena, cambiarDatos
                )
            }

            if (!uiState.recuperacionExitosa) {
                TextoEnlace(
                    texto = "",
                    textoEnlace = stringResource(R.string.auth_recuperacion_volver),
                    onClick = volverAlLogin
                )
            }

            Spacer(Modifier.size(Theme.dimens.spacingL))
        }
    }
}

// ---------- previews ----------
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewRecuperacionIdentidad() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(
                preguntas = listOf(
                    PreguntaRecuperacion(1, stringResource(R.string.auth_pregunta_ejemplo_mascota)),
                    PreguntaRecuperacion(2, stringResource(R.string.auth_pregunta_ejemplo_ciudad))
                )
            ),
            setCorreo = {}, validarCorreoField = {}, setPreguntaRecuperacion = {},
            setRespuesta = {}, validarRespuestaField = {}, validarIdentidad = {},
            cambiarDatos = {}, setNuevaContrasena = {}, validarContrasenaField = {},
            setConfirmarContrasena = {}, validarConfirmacionField = {},
            restablecerContrasena = {}, volverAlLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewRecuperacionContrasena() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(
                correo = "prueba@correo.ejemplo",
                identidadValidada = true
            ),
            setCorreo = {}, validarCorreoField = {}, setPreguntaRecuperacion = {},
            setRespuesta = {}, validarRespuestaField = {}, validarIdentidad = {},
            cambiarDatos = {}, setNuevaContrasena = {}, validarContrasenaField = {},
            setConfirmarContrasena = {}, validarConfirmacionField = {},
            restablecerContrasena = {}, volverAlLogin = {}
        )
    }
}

