package com.pagoflex.app.core.ui.components.chips

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso

@Composable
fun StatusChip(
    label: String,
    colores: CardColors,
    modifier: Modifier = Modifier,
    showIndicator: Boolean = true,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = colores.containerColor,
        contentColor = colores.contentColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (showIndicator) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(colores.contentColor, CircleShape),
                )
            }

            Text(
                text = label,
                style = Theme.type.labelMedium,
            )
        }
    }
}

// ----------------- PREVIEWS -----------------

@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun StatusChipPreview() {
    val pendiente = EstadoCompromiso.PENDIENTE
    val pagado = EstadoCompromiso.PAGADO
    val atrasado = EstadoCompromiso.VENCIDO

    PagoFlexTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatusChip(
                    label = stringResource(pendiente.labelRes),
                    colores = pendiente.coloresCard
                )
                StatusChip(
                    label = stringResource(pagado.labelRes),
                    colores = pagado.coloresCard
                )
                StatusChip(
                    label = stringResource(atrasado.labelRes),
                    colores = atrasado.coloresCard
                )
            }
        }
    }
}