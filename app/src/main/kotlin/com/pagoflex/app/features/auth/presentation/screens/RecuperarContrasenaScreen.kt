package com.pagoflex.app.features.auth.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.components.FormularioCodigoRecuperacion
import com.pagoflex.app.features.auth.presentation.components.FormularioIdentidad
import com.pagoflex.app.features.auth.presentation.components.FormularioNuevaContrasena
import com.pagoflex.app.features.auth.presentation.model.PasoRecuperacion
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState
import com.pagoflex.app.features.auth.presentation.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    viewModel: RecuperarContrasenaViewModel,
    volverAlLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val salir = {
        viewModel.reiniciar()
        volverAlLogin()
    }
    val atras = {
        if (uiState.paso == PasoRecuperacion.CORREO || uiState.paso == PasoRecuperacion.EXITO) salir()
        else viewModel.volverPasoAnterior()
    }
    BackHandler(enabled = !uiState.cargando) {
        atras()
    }

    RecuperarContrasenaContent(
        uiState = uiState,
        setCorreo = viewModel::setCorreo,
        validarCorreoField = viewModel::validarCorreoField,
        solicitarCodigo = viewModel::solicitarCodigo,
        setCodigo = viewModel::setCodigo,
        verificarCodigo = viewModel::verificarCodigo,
        reenviarCodigo = viewModel::reenviarCodigo,
        setNuevaContrasena = viewModel::setNuevaContrasena,
        validarContrasenaField = viewModel::validarContrasenaField,
        setConfirmarContrasena = viewModel::setConfirmarContrasena,
        validarConfirmacionField = viewModel::validarConfirmacionField,
        restablecerContrasena = viewModel::restablecerContrasena,
        volverAtras = atras,
        volverAlLogin = salir
    )
}

@Composable
private fun RecuperarContrasenaContent(
    uiState: RecuperarContrasenaUiState,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit,
    solicitarCodigo: () -> Unit,
    setCodigo: (String) -> Unit,
    verificarCodigo: () -> Unit,
    reenviarCodigo: () -> Unit,
    setNuevaContrasena: (String) -> Unit,
    validarContrasenaField: () -> Unit,
    setConfirmarContrasena: (String) -> Unit,
    validarConfirmacionField: () -> Unit,
    restablecerContrasena: () -> Unit,
    volverAtras: () -> Unit,
    volverAlLogin: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = Theme.dimens.spacingM, vertical = Theme.dimens.spacingL),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = when (uiState.paso) {
                        PasoRecuperacion.CORREO -> solicitarCodigo
                        PasoRecuperacion.CODIGO -> verificarCodigo
                        PasoRecuperacion.CONTRASENA -> restablecerContrasena
                        PasoRecuperacion.EXITO -> volverAlLogin
                    },
                    enabled = !uiState.cargando &&
                        (uiState.paso != PasoRecuperacion.CODIGO || !uiState.requiereNuevoCodigo),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(when (uiState.paso) {
                            PasoRecuperacion.CORREO -> R.string.auth_api_enviar_codigo
                            PasoRecuperacion.CODIGO -> R.string.auth_api_verificar_codigo
                            PasoRecuperacion.CONTRASENA -> R.string.auth_recuperacion_guardar
                            PasoRecuperacion.EXITO -> R.string.auth_recuperacion_volver
                        }))
                    }
                }
                if (uiState.paso == PasoRecuperacion.CODIGO) {
                    TextButton(
                        onClick = reenviarCodigo,
                        enabled = !uiState.cargando
                    ) {
                        Text(stringResource(R.string.auth_api_reenviar_codigo))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.dimens.spacingM),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)
        ) {
            IconButton(onClick = volverAtras, enabled = !uiState.cargando) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.auth_recuperacion_volver_atras),
                    tint = Theme.colores.onSurface
                )
            }
            Text(
                stringResource(R.string.auth_recuperacion_titulo),
                style = Theme.type.headlineLarge
            )
            when (uiState.paso) {
                PasoRecuperacion.CORREO -> FormularioIdentidad(
                    uiState, setCorreo, validarCorreoField
                )
                PasoRecuperacion.CODIGO -> FormularioCodigoRecuperacion(
                    uiState, setCodigo, verificarCodigo
                )
                PasoRecuperacion.CONTRASENA -> FormularioNuevaContrasena(
                    uiState, setNuevaContrasena, validarContrasenaField,
                    setConfirmarContrasena, validarConfirmacionField
                )
                PasoRecuperacion.EXITO -> Text(
                    stringResource(R.string.auth_recuperacion_exito),
                    style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
                )
            }
            Spacer(Modifier.size(Theme.dimens.spacingL))
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewRecuperacionIdentidad() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(),
            setCorreo = {}, validarCorreoField = {}, solicitarCodigo = {},
            setCodigo = {}, verificarCodigo = {}, reenviarCodigo = {},
            setNuevaContrasena = {}, validarContrasenaField = {},
            setConfirmarContrasena = {}, validarConfirmacionField = {},
            restablecerContrasena = {}, volverAtras = {}, volverAlLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewRecuperacionCodigo() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(correo = "ejemplo@correo.cl", paso = PasoRecuperacion.CODIGO),
            setCorreo = {}, validarCorreoField = {}, solicitarCodigo = {},
            setCodigo = {}, verificarCodigo = {}, reenviarCodigo = {},
            setNuevaContrasena = {}, validarContrasenaField = {},
            setConfirmarContrasena = {}, validarConfirmacionField = {},
            restablecerContrasena = {}, volverAtras = {}, volverAlLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewRecuperacionContrasena() {
    PagoFlexTheme {
        RecuperarContrasenaContent(
            uiState = RecuperarContrasenaUiState(correo = "ejemplo@correo.cl", paso = PasoRecuperacion.CONTRASENA),
            setCorreo = {}, validarCorreoField = {}, solicitarCodigo = {},
            setCodigo = {}, verificarCodigo = {}, reenviarCodigo = {},
            setNuevaContrasena = {}, validarContrasenaField = {},
            setConfirmarContrasena = {}, validarConfirmacionField = {},
            restablecerContrasena = {}, volverAtras = {}, volverAlLogin = {}
        )
    }
}
