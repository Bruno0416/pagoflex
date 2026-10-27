package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.inputs.SelectField
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState

@Composable
fun FormularioIdentidad(
    uiState: RecuperarContrasenaUiState,
    setCorreo: (String) -> Unit,
    validarCorreoField: () -> Unit,
    setPreguntaRecuperacion: (Int) -> Unit,
    setRespuesta: (String) -> Unit,
    validarRespuestaField: () -> Unit,
    validarIdentidad: () -> Unit
) {
    Text(
        stringResource(R.string.auth_recuperacion_descripcion),
        style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
    )

    TextField(
        label = stringResource(R.string.auth_login_correo),
        value = uiState.correo,
        onValueChange = setCorreo,
        placeholder = stringResource(R.string.auth_login_correo_placeholder),
        onUnfocus = validarCorreoField,
        error = uiState.errorCorreo?.let { stringResource(it) },
        enabled = !uiState.cargando
    )

    SelectField(
        modifier = Modifier.fillMaxWidth(),
        selectedOption = uiState.preguntas
            .firstOrNull { it.idPreguntaRecuperacion == uiState.idPreguntaRecuperacion }
            ?.pregunta.orEmpty(),
        options = uiState.preguntas.map { it.pregunta },
        onOptionSelected = { pregunta ->
            uiState.preguntas.firstOrNull { it.pregunta == pregunta }?.let {
                setPreguntaRecuperacion(it.idPreguntaRecuperacion)
            }
        },
        label = stringResource(R.string.auth_registro_pregunta_recuperacion),
        placeholder = stringResource(R.string.auth_registro_seleccionar_pregunta),
        enabled = !uiState.cargando && uiState.preguntas.isNotEmpty()
    )

    uiState.errorPregunta?.let {
        Text(stringResource(it), color = Theme.colores.error)
    }

    TextField(
        label = stringResource(R.string.auth_registro_respuesta_recuperacion),
        value = uiState.respuesta,
        onValueChange = setRespuesta,
        placeholder = stringResource(R.string.auth_registro_respuesta_placeholder),
        onUnfocus = validarRespuestaField,
        error = uiState.errorRespuesta?.let { stringResource(it) },
        enabled = !uiState.cargando
    )

    uiState.errorRecuperacion?.let { errorRes ->
        Text(stringResource(errorRes), color = Theme.colores.error)
    }

    Button(onClick = validarIdentidad, enabled = !uiState.cargando) {
        if (uiState.cargando) CircularProgressIndicator()
        else Text(stringResource(R.string.auth_recuperacion_continuar))
    }
}
