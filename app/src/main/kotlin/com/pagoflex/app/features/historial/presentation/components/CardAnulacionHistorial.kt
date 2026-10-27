package com.pagoflex.app.features.historial.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.avatar.Avatar
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.historial.data.AnulacionHistorialRow
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.format.DateTimeFormatter

@Composable
fun CardAnulacionHistorial(anulacion: AnulacionHistorialRow) {
    val colores = EstadoCompromiso.ANULADO.coloresCard
    val siglas = anulacion.empresa.split(" ")
        .filter { it.isNotBlank() }
        .let { palabras ->
            if (palabras.size > 1) palabras.first().take(1) + palabras.last().take(1)
            else palabras.firstOrNull()?.take(2).orEmpty()
        }.uppercase()
    val cuota = if (anulacion.numeroCuota != null && anulacion.totalCuotas != null) {
        " · ${anulacion.numeroCuota}/${anulacion.totalCuotas}"
    } else ""

    Card(modifier = Modifier.fillMaxWidth(), variant = ComponentVariant.Primary) {
        Row(
            modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso),
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(fallback = siglas, colores = colores)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    anulacion.empresa, style = Theme.type.titleMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${anulacion.concepto}$cuota", style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant, maxLines = 2
                )
                Text(
                    anulacion.fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
            }

            StatusChip(
                label = stringResource(R.string.home_estado_anulado),
                colores = colores
            )
        }
    }
}
