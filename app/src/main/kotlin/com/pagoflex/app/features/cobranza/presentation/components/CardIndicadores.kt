package com.pagoflex.app.features.cobranza.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.pagoflex.app.R
import com.pagoflex.app.core.data.remote.externa.IndicadoresDia
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.core.ui.theme.Theme
import java.text.NumberFormat
import java.util.Locale

// uf y dólar del día desde mindicador.cl, con lo recaudado convertido a uf
@Composable
fun CardIndicadores(
    indicadores: IndicadoresDia,
    montoRecaudado: Long,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Theme.colores.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
        ) {
            Text(
                stringResource(R.string.cob_indicadores_titulo, indicadores.fecha.aTexto()),
                style = Theme.type.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.cob_indicadores_uf, indicadores.uf.aPesosConDecimales()),
                    style = Theme.type.bodyLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    stringResource(R.string.cob_indicadores_dolar, indicadores.dolar.aPesosConDecimales()),
                    style = Theme.type.bodyLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
            if (montoRecaudado > 0) {
                Text(
                    stringResource(R.string.cob_indicadores_recaudado_uf, (montoRecaudado / indicadores.uf).aUf()),
                    style = Theme.type.bodyMedium,
                )
            }
            Text(stringResource(R.string.cob_indicadores_fuente), style = Theme.type.labelSmall)
        }
    }
}

// --- formato ---
private val localeChile: Locale = Locale.forLanguageTag("es-CL")

// 41384.81 -> "$ 41.384,81"
private fun Double.aPesosConDecimales(): String = "$ " + NumberFormat.getNumberInstance(localeChile).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}.format(this)

// 784.7312 -> "784,7"
private fun Double.aUf(): String = NumberFormat.getNumberInstance(localeChile).apply {
    minimumFractionDigits = 1
    maximumFractionDigits = 1
}.format(this)
