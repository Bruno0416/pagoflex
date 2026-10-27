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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.selection.Checkbox
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.components.ContrasenasRegistro
import com.pagoflex.app.features.auth.presentation.components.DatosPersonalesRegistro
import com.pagoflex.app.features.auth.presentation.components.Logo
import com.pagoflex.app.features.auth.presentation.components.RecuperacionRegistro
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
        setPreguntaRecuperacion = viewModel::setPreguntaRecuperacion,
        setRespuestaRecuperacion = viewModel::setRespuestaRecuperacion,
        validarRespuestaRecuperacionField = viewModel::validarRespuestaRecuperacionField,
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
    setPreguntaRecuperacion: (Int) -> Unit,
    setRespuestaRecuperacion: (String) -> Unit,
    validarRespuestaRecuperacionField: () -> Unit,

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
                Modifier.size(Theme.dimens.spacingXL)
            )

            Logo()

            Spacer(
                Modifier.size(Theme.dimens.spacingL)
            )

            Text(
                stringResource(
                    R.string.auth_registro_titulo
                ),
                style = Theme.type.headlineLarge
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

            DatosPersonalesRegistro(
                uiState, setNombres, validarNombresField,
                setApellidos, validarApellidosField,
                setRut, validarRutField, setCorreo, validarCorreoField
            )
            ContrasenasRegistro(
                uiState, setContrasena, validarContrasenaField,
                setConfirmarContrasena, validarConfirmarContrasenaField
            )
            RecuperacionRegistro(
                uiState, setPreguntaRecuperacion,
                setRespuestaRecuperacion, validarRespuestaRecuperacionField
            )

            // ---------- terminos y condiciones ----------
            Checkbox(
                uiState.terminosYCondiciones,
                setTerminosYCondiciones,
                label = stringResource(R.string.auth_registro_terminos_condiciones)
            )

            uiState.errorTerminosYCondiciones?.let {
                Text(
                    stringResource(R.string.auth_error_terminos_y_condiciones),
                    style = Theme.type.labelLarge.copy(
                        color = Theme.colores.error,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            // ---------- error formulario ----------
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

            // ---------- boton ----------
            Button(
                onClick = registrar,
                enabled = !uiState.cargando
            ) {
                if (uiState.cargando) {
                    CircularProgressIndicator()
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


// ---------- preview ----------
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
            uiState = RegisterUiState(
                preguntas = listOf(
                    PreguntaRecuperacion(1, stringResource(R.string.auth_pregunta_ejemplo_mascota)),
                    PreguntaRecuperacion(2, stringResource(R.string.auth_pregunta_ejemplo_ciudad))
                ),
                idPreguntaRecuperacion = 1
            ),
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
            setPreguntaRecuperacion = {},
            setRespuestaRecuperacion = {},
            validarRespuestaRecuperacionField = {},
            setTerminosYCondiciones = {},
            registrar = {},
            volverAlLogin = {}
        )
    }
}
