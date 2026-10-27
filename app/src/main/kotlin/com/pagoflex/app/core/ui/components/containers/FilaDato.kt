package com.pagoflex.app.core.ui.components.containers

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

// fila "etiqueta .... valor" para las pantallas de detalle
@Composable
fun FilaDato(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimens.spacingXXS),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM),
    ) {
        Text(
            text = etiqueta,
            style = Theme.type.bodyMedium,
            color = Theme.colores.onSurfaceVariant,
        )
        Text(
            text = valor,
            style = if (destacado) {
                Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                Theme.type.bodyMedium
            },
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

// tarjeta con título y filas de datos
@Composable
fun SeccionDatos(
    titulo: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(variant = ComponentVariant.Primary, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso)) {
            Text(
                text = titulo,
                style = Theme.type.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = Theme.dimens.spacingXS),
            )
            content()
        }
    }
}

// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SeccionDatosPreview() {
    PagoFlexTheme {
        Surface {
            SeccionDatos(titulo = "Compromiso", modifier = Modifier.padding(16.dp)) {
                FilaDato("Folio", "CP-2026-0106")
                FilaDato("Monto", "$ 310.000")
                FilaDato("Total a pagar", "$ 318.500", destacado = true)
            }
        }
    }
}
