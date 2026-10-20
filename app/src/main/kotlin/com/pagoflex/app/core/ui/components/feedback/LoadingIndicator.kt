package com.pagoflex.app.core.ui.components.feedback

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.ContainedLoadingIndicator as MaterialContainedLoadingIndicator
import androidx.compose.material3.LoadingIndicator as MaterialLoadingIndicator

enum class LoadingIndicatorStyle {
    Standard,
    Contained
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    variant: ComponentVariant = ComponentVariant.Primary,
    style: LoadingIndicatorStyle = LoadingIndicatorStyle.Standard,
    progress: (() -> Float)? = null,
) {
    val indicatorColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.secondary
        ComponentVariant.Destructive -> Theme.colores.error
    }

    val containerColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primaryContainer
        ComponentVariant.Secondary -> Theme.colores.secondaryContainer
        ComponentVariant.Destructive -> Theme.colores.errorContainer
    }

    val containedIndicatorColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.onPrimaryContainer
        ComponentVariant.Secondary -> Theme.colores.onSecondaryContainer
        ComponentVariant.Destructive -> Theme.colores.onErrorContainer
    }

    when (style) {
        LoadingIndicatorStyle.Standard -> {
            if (progress == null) {
                MaterialLoadingIndicator(
                    modifier = modifier,
                    color = indicatorColor,
                )
            } else {
                MaterialLoadingIndicator(
                    progress = progress,
                    modifier = modifier,
                    color = indicatorColor,
                )
            }
        }

        LoadingIndicatorStyle.Contained -> {
            if (progress == null) {
                MaterialContainedLoadingIndicator(
                    modifier = modifier,
                    containerColor = containerColor,
                    indicatorColor = containedIndicatorColor,
                )
            } else {
                MaterialContainedLoadingIndicator(
                    progress = progress,
                    modifier = modifier,
                    containerColor = containerColor,
                    indicatorColor = containedIndicatorColor,
                )
            }
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
private fun LoadingIndicatorPreview() {
    PagoFlexTheme {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    LoadingIndicator(variant = ComponentVariant.Primary)
                    LoadingIndicator(variant = ComponentVariant.Secondary)
                    LoadingIndicator(variant = ComponentVariant.Destructive)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    LoadingIndicator(
                        variant = ComponentVariant.Primary,
                        style = LoadingIndicatorStyle.Contained,
                    )
                    LoadingIndicator(
                        variant = ComponentVariant.Secondary,
                        style = LoadingIndicatorStyle.Contained,
                    )
                    LoadingIndicator(
                        variant = ComponentVariant.Destructive,
                        style = LoadingIndicatorStyle.Contained,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun DeterminateLoadingIndicatorPreview() {
    PagoFlexTheme {
        Surface {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                LoadingIndicator(progress = { 0.65f })
                LoadingIndicator(
                    progress = { 0.65f },
                    style = LoadingIndicatorStyle.Contained,
                )
            }
        }
    }
}
