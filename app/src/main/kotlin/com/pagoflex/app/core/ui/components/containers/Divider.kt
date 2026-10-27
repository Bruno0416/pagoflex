package com.pagoflex.app.core.ui.components.containers

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.HorizontalDivider as MaterialHorizontalDivider

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = Theme.colores.outlineVariant,
) {
    MaterialHorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
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
private fun DividerPreview() {
    PagoFlexTheme {
        Surface {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text("Elemento superior", modifier = Modifier.padding(16.dp))
                Divider(modifier = Modifier.fillMaxWidth())
                Text("Elemento inferior", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
