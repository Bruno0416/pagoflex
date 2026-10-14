package com.pagoflex.app.core.ui.components.containers

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.CardShape
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.Card as MaterialCard


@Composable
fun Card(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    colors: CardColors = CardDefaults.cardColors(),
    shape: Shape = CardShape,
    border: BorderStroke? = null,
    elevation: CardElevation = CardDefaults.cardElevation(),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val colores: CardColors = when (variant) {
        ComponentVariant.Primary -> CardDefaults.cardColors(
            containerColor = Theme.colores.surfaceContainerHigh,
            contentColor = Theme.colores.onSurface
        )

        ComponentVariant.Secondary -> CardDefaults.cardColors(
            containerColor = Theme.colores.surfaceContainerHighest,
            contentColor = Theme.colores.onSurfaceVariant
        )

        ComponentVariant.Destructive -> CardDefaults.cardColors(
            containerColor = Theme.colores.errorContainer,
            contentColor = Theme.colores.onErrorContainer
        )

        else -> colors
    }

    if (onClick != null) {
        MaterialCard(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = colores,
            elevation = elevation,
            border = border,
            content = content
        )
    } else {
        MaterialCard(
            modifier = modifier,
            shape = shape,
            colors = colores,
            elevation = elevation,
            border = border,
            content = content
        )
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
private fun PrimaryCardPreview() {
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Primary,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Tarjeta Primaria", Modifier.padding(16.dp))
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
private fun SecondaryCardPreview() {
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Secondary,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Tarjeta Secundaria", Modifier.padding(16.dp))
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
private fun DestructiveCardPreview() {
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Destructive,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Tarjeta Destructiva", Modifier.padding(16.dp))
        }
    }
}