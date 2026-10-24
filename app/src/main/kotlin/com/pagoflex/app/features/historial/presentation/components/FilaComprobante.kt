package com.pagoflex.app.features.historial.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun FilaComprobante(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            style = Theme.type.bodyMedium,
            color = Theme.colores.onSurfaceVariant
        )
        Text(
            text = valor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            style = if (destacado) {
                Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else Theme.type.bodyMedium,
        )
    }
}
