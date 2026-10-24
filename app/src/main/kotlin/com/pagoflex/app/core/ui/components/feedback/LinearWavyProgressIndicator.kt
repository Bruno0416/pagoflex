package com.pagoflex.app.core.ui.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import androidx.compose.material3.LinearWavyProgressIndicator as MaterialLinearWavyProgressIndicator

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
