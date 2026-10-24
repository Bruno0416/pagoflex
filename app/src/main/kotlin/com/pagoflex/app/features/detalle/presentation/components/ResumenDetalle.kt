package com.pagoflex.app.features.detalle.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.containers.Divider
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.detalle.data.DetalleCompromisoRow
import com.pagoflex.app.features.detalle.presentation.model.DetalleUiState
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResumenDetalle(
    detalle: DetalleCompromisoRow,
    uiState: DetalleUiState,
    estado: EstadoCompromiso?
) {
    val coloresEstado = estado?.coloresCard

    val siglas = detalle.organizacion
        .split(" ")
        .filter { it.isNotBlank() }
        .let { palabras ->
            if (palabras.size >= 2) {
                palabras.first().take(1) + palabras.last().take(1)
            } else {
                palabras.firstOrNull()?.take(2).orEmpty()
            }
        }.uppercase()

    val cuota = if (
        detalle.numeroCuota != null &&
        detalle.totalCuotas != null
    ) {
        stringResource(
            R.string.detalle_cuota,
            detalle.numeroCuota,
            detalle.totalCuotas
        )
    } else {
        ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        variant = ComponentVariant.Primary
    ) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(
                Theme.dimens.spacingM
            )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    Theme.dimens.spacingM
                )
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = coloresEstado?.containerColor
                            ?: Theme.colores.secondaryContainer,
                        contentColor = coloresEstado?.contentColor
                            ?: Theme.colores.onSecondaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = siglas,
                            style = Theme.type.titleMedium
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = detalle.organizacion,
                        style = Theme.type.titleLarge
                    )

                    Text(
                        text = "${detalle.concepto}$cuota",
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.onSurfaceVariant
                    )
                }
            }

            // ---------- chip de estado ----------
            if (estado != null) {
                StatusChip(
                    label = stringResource(estado.labelRes),
                    colores = estado.coloresCard,
                    modifier = Modifier.align(Alignment.Start)
                )
            } else {
                Text(
                    text = uiState.estadoActual,
                    style = Theme.type.labelMedium
                )
            }

            Divider()

            FilaDetalle(
                etiqueta = stringResource(R.string.detalle_monto),
                valor = detalle.monto.toLong().formatoCLP()
            )

            FilaDetalle(
                etiqueta = stringResource(R.string.detalle_recargo),
                valor = uiState.recargo.toLong().formatoCLP()
            )

            if (estado == EstadoCompromiso.VENCIDO) {
                FilaDetalle(
                    etiqueta = stringResource(R.string.detalle_atraso),
                    valor = stringResource(
                        R.string.detalle_dias,
                        uiState.diasAtraso
                    )
                )
            }

            Divider()

            FilaDetalle(
                etiqueta = stringResource(R.string.detalle_total),
                valor = uiState.total.formatoCLP(),
                destacado = true
            )
        }
    }
}

// --- formato clp ---
private fun Long.formatoCLP(): String = "$ " +
        NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL")).format(this)
