package com.pagoflex.app.features.historial.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.inputs.SelectField
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun FiltrosHistorial(
    empresaSeleccionada: String,
    empresas: List<String>,
    onEmpresaSeleccionada: (String) -> Unit,
    anioSeleccionado: String,
    anios: List<String>,
    onAnioSeleccionado: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)) {
        Text(stringResource(R.string.historial_titulo), style = Theme.type.headlineMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)) {
            SelectField(
                selectedOption = empresaSeleccionada,
                options = empresas,
                onOptionSelected = onEmpresaSeleccionada,
                modifier = Modifier.weight(1f)
            )
            SelectField(
                selectedOption = anioSeleccionado,
                options = anios,
                onOptionSelected = onAnioSeleccionado,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            stringResource(R.string.historial_descripcion),
            style = Theme.type.bodyMedium,
            color = Theme.colores.onSurfaceVariant
        )
    }
}
