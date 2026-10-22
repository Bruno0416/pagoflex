package com.pagoflex.app.core.ui.components.chips

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

// filtros con scroll horizontal para encontrar rápido lo pendiente (RNF-14)
@Composable
fun <T> FiltroChips(
    opciones: List<T>,
    seleccionada: T,
    onSeleccion: (T) -> Unit,
    texto: (T) -> String,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXS),
        contentPadding = PaddingValues(horizontal = Theme.dimens.spacingM),
    ) {
        items(opciones) { opcion ->
            FilterChip(
                selected = opcion == seleccionada,
                onClick = { onSeleccion(opcion) },
                label = { Text(texto(opcion), style = Theme.type.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Theme.colores.primaryContainer,
                    selectedLabelColor = Theme.colores.onPrimaryContainer,
                ),
            )
        }
    }
}

// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FiltroChipsPreview() {
    var seleccionada by remember { mutableStateOf("Todos") }
    PagoFlexTheme {
        Surface {
            FiltroChips(
                opciones = listOf("Todos", "Pendiente", "Vencido", "Pagado"),
                seleccionada = seleccionada,
                onSeleccion = { seleccionada = it },
                texto = { it },
            )
        }
    }
}
