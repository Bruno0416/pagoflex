package com.pagoflex.app.core.ui.components.feedback

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator as MaterialCircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator as MaterialCircularWavyProgressIndicator
import androidx.compose.material3.LinearProgressIndicator as MaterialLinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator as MaterialLinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
private fun progressColors(variant: ComponentVariant) = when (variant) {
    ComponentVariant.Primary -> Theme.colores.primary to Theme.colores.primaryContainer
    ComponentVariant.Secondary -> Theme.colores.secondary to Theme.colores.secondaryContainer
    ComponentVariant.Destructive -> Theme.colores.error to Theme.colores.errorContainer
}

@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    variant: ComponentVariant = ComponentVariant.Primary,
) {
    val (color, trackColor) = progressColors(variant)

    if (progress == null) {
        MaterialLinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        MaterialLinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    variant: ComponentVariant = ComponentVariant.Primary,
) {
    val (color, trackColor) = progressColors(variant)

    if (progress == null) {
        MaterialCircularProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        MaterialCircularProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

@Composable
fun LinearWavyProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    variant: ComponentVariant = ComponentVariant.Primary,
) {
    val (color, trackColor) = progressColors(variant)

    if (progress == null) {
        MaterialLinearWavyProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        MaterialLinearWavyProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

@Composable
fun CircularWavyProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    variant: ComponentVariant = ComponentVariant.Primary,
) {
    val (color, trackColor) = progressColors(variant)

    if (progress == null) {
        MaterialCircularWavyProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        MaterialCircularWavyProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

// ----------------- PREVIEWS -----------------

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
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
