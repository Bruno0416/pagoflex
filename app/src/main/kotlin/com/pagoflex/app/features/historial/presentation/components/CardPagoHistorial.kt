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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.ui.components.avatar.Avatar
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.format.DateTimeFormatter

@Composable
fun CardPagoHistorial(
    comprobante: Comprobante,
    onClick: () -> Unit
) {
    val pagadoATiempo = !comprobante.fechaHora.toLocalDate().isAfter(comprobante.fechaVencimiento)
    val colores = if (pagadoATiempo) {
        EstadoCompromiso.PAGADO.coloresCard
    } else {
        EstadoCompromiso.VENCIDO.coloresCard
    }
    val siglas = comprobante.empresa.split(" ")
        .filter { it.isNotBlank() }
        .let { palabras ->
            if (palabras.size > 1) palabras.first().take(1) + palabras.last().take(1)
            else palabras.firstOrNull()?.take(2).orEmpty()
        }.uppercase()
    val cuota = if (comprobante.numeroCuota != null && comprobante.totalCuotas != null) {
        " · ${comprobante.numeroCuota}/${comprobante.totalCuotas}"
    } else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        variant = ComponentVariant.Primary,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso),
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(fallback = siglas, colores = EstadoCompromiso.PAGADO.coloresCard)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    comprobante.empresa, style = Theme.type.titleMedium, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${comprobante.concepto}$cuota", style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant, maxLines = 2
                )
                Text(
                    "${comprobante.fechaHora.format(DateTimeFormatter.ofPattern("dd-MM"))} · ${comprobante.canal}",
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXXS)
            ) {
                Text(
                    comprobante.montoPagado.toLong().formatoCLP(),
                    style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                StatusChip(
                    label = if (pagadoATiempo) stringResource(R.string.historial_a_tiempo)
                    else stringResource(R.string.historial_con_atraso),
                    colores = colores
                )
            }
        }
    }
}

