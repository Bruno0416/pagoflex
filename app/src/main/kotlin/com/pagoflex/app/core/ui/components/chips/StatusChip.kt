package com.pagoflex.app.core.ui.components.chips

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.theme.ChipAtrasadoContainer
import com.pagoflex.app.core.ui.theme.ChipAtrasadoContent
import com.pagoflex.app.core.ui.theme.ChipPagadoContainer
import com.pagoflex.app.core.ui.theme.ChipPagadoContent
import com.pagoflex.app.core.ui.theme.ChipPendienteContainer
import com.pagoflex.app.core.ui.theme.ChipPendienteContent
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun StatusChip(
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    showIndicator: Boolean = true,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
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
                        .background(contentColor, CircleShape),
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun StatusChipPreview() {
    PagoFlexTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusChip(
                label = "Pendiente",
                containerColor = ChipPendienteContainer,
                contentColor = ChipPendienteContent,
            )
            StatusChip(
                label = "Pagado",
                containerColor = ChipPagadoContainer,
                contentColor = ChipPagadoContent,
            )
            StatusChip(
                label = "Atrasado",
                containerColor = ChipAtrasadoContainer,
                contentColor = ChipAtrasadoContent,
            )
        }
    }
}
