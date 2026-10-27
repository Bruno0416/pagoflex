package com.pagoflex.app.core.ui.components.feedback

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme

@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun ProgressIndicatorPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { 0.68f },
                    variant = ComponentVariant.Secondary,
                )
                LinearWavyProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { 0.68f },
                )

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    CircularProgressIndicator()
                    CircularProgressIndicator(
                        progress = { 0.68f },
                        variant = ComponentVariant.Secondary,
                    )
                    CircularWavyProgressIndicator(
                        progress = { 0.68f },
                        variant = ComponentVariant.Destructive,
                    )
                }
            }
        }
    }
}
