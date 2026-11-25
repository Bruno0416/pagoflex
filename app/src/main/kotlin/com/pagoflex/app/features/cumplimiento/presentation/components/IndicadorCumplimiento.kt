package com.pagoflex.app.features.cumplimiento.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.cumplimiento.data.remote.dto.response.CumplimientoResponse
import java.util.Locale

@Composable
internal fun IndicadorCumplimiento(resultado: CumplimientoResponse) {
    val porcentaje = resultado.porcentaje ?: return
    val categoria = when (resultado.categoria) {
        "EJEMPLAR" -> R.string.cumplimiento_categoria_ejemplar
        "AL_DIA" -> R.string.cumplimiento_categoria_al_dia
        "EN_CAMINO" -> R.string.cumplimiento_categoria_en_camino
        else -> return
    }

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { (porcentaje / 100.0).toFloat() },
            modifier = Modifier.size(206.dp),
            strokeWidth = 12.dp,
            color = Theme.colores.primary,
            trackColor = Theme.colores.primaryContainer,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(Locale.forLanguageTag("es-CL"), "%.1f%%", porcentaje),
                style = Theme.type.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = Theme.colores.onSurface
            )
            Text(
                text = stringResource(categoria),
                style = Theme.type.titleLarge,
                color = Theme.colores.primary
            )
        }
    }
}
