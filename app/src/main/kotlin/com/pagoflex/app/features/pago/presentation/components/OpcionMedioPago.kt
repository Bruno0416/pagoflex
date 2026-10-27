package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.selection.RadioButton
import com.pagoflex.app.core.ui.theme.Theme

@Composable
internal fun OpcionMedioPago(
    nombre: String,
    seleccionado: Boolean,
    habilitado: Boolean,
    onSeleccionar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) Theme.colores.primaryContainer else Theme.colores.surface,
            contentColor = Theme.colores.onSurface
        ),
        border = BorderStroke(
            if (seleccionado) 2.dp else 1.dp,
            if (seleccionado) Theme.colores.primary else Theme.colores.outlineVariant
        )
    ) {
        RadioButton(
            selected = seleccionado,
            onClick = onSeleccionar,
            enabled = habilitado,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.dimens.spacingM),
            label = nombre
        )
    }
}
