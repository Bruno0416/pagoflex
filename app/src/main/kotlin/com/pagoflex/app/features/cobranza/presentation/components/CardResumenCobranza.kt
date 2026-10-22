package com.pagoflex.app.features.cobranza.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.ResumenCobranza
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.styles.ChipColors
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aPorcentaje
import com.pagoflex.app.core.utils.aTexto
import java.time.YearMonth

// selector de mes: < septiembre 2026 >
@Composable
fun SelectorMes(
    mes: YearMonth,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onAnterior) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.cob_mes_anterior),
                modifier = Modifier.rotate(180f),
            )
        }
        Text(text = mes.aTexto(), style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold))
        IconButton(onClick = onSiguiente) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.cob_mes_siguiente),
            )
        }
    }
}

// resumen de la cobranza del mes (RF-20)
@Composable
fun CardResumenCobranza(
    resumen: ResumenCobranza,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colores.primaryContainer,
            contentColor = Theme.colores.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
        ) {
            Text(stringResource(R.string.cob_tasa), style = Theme.type.labelLarge)
            Text(
                text = resumen.tasaCobranza?.aPorcentaje() ?: stringResource(R.string.cob_sin_compromisos_mes),
                style = Theme.type.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
            LinearProgressIndicator(
                progress = { ((resumen.tasaCobranza ?: 0.0) / 100).toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.cob_recaudado, resumen.montoRecaudado.aPesos()),
                style = Theme.type.bodyMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXS),
            ) {
                Indicador(stringResource(R.string.cob_pagados), resumen.pagados, ChipColors.pagadoContainer, ChipColors.pagadoContent, Modifier.weight(1f))
                Indicador(stringResource(R.string.cob_pendientes), resumen.pendientes, ChipColors.pendienteContainer, ChipColors.pendienteContent, Modifier.weight(1f))
                Indicador(stringResource(R.string.cob_vencidos), resumen.vencidos, ChipColors.expiradoContainer, ChipColors.expiradoContent, Modifier.weight(1f))
            }

            // los en revisión ya están contados en vencidos o pendientes
            Text(
                text = stringResource(
                    R.string.cob_resumen_detalle,
                    resumen.totalCompromisos,
                    resumen.anulados,
                    resumen.enRevision
                ),
                style = Theme.type.bodySmall,
            )
        }
    }
}

@Composable
private fun Indicador(
    etiqueta: String,
    valor: Int,
    contenedor: Color,
    contenido: Color,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier, color = contenedor, contentColor = contenido, shape = Theme.formas.medium) {
        Column(
            modifier = Modifier.padding(vertical = Theme.dimens.spacingS),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(valor.toString(), style = Theme.type.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text(etiqueta, style = Theme.type.labelSmall)
        }
    }
}

// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CardResumenCobranzaPreview() {
    PagoFlexTheme {
        Surface {
            CardResumenCobranza(
                resumen = ResumenCobranza(
                    mes = YearMonth.of(2026, 9),
                    totalCompromisos = 120,
                    anulados = 2,
                    pagados = 104,
                    vencidos = 14,
                    enRevision = 1,
                    montoRecaudado = 32_476_000,
                    tasaCobranza = 88.1,
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
