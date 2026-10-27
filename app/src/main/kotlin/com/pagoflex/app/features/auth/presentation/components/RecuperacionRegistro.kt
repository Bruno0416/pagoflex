package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.inputs.SelectField
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState

@Composable
fun RecuperacionRegistro(
    uiState: RegisterUiState,
    setPreguntaRecuperacion: (Int) -> Unit,
    setRespuestaRecuperacion: (String) -> Unit,
    validarRespuestaRecuperacionField: () -> Unit
) {
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

    uiState.errorPreguntaRecuperacion?.let {
        Text(stringResource(it), color = Theme.colores.error)
    }

    TextField(
        label = stringResource(R.string.auth_registro_respuesta_recuperacion),
        value = uiState.respuestaRecuperacion,
        onValueChange = setRespuestaRecuperacion,
        placeholder = stringResource(R.string.auth_registro_respuesta_placeholder),
        onUnfocus = validarRespuestaRecuperacionField,
        error = uiState.errorRespuestaRecuperacion?.let { stringResource(it) }
    )

}
