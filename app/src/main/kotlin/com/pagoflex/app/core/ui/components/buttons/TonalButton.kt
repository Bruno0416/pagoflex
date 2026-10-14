package com.pagoflex.app.core.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.model.buttonSize
import com.pagoflex.app.core.ui.theme.ButtonShape
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.FilledTonalButton as MaterialFilledTonalButton


@Composable
fun TonalButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    size: ComponentSize = ComponentSize.Default,
    content: @Composable RowScope.() -> Unit,
) {
    val colores = when (variant) {
        ComponentVariant.Primary -> ButtonDefaults.filledTonalButtonColors(
            containerColor = Theme.colores.primaryContainer,
            contentColor = Theme.colores.onPrimaryContainer,
            disabledContainerColor = Theme.colores.primaryContainer.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onPrimaryContainer.copy(alpha = 0.50f)
        )

        ComponentVariant.Secondary -> ButtonDefaults.filledTonalButtonColors(
            containerColor = Theme.colores.secondaryContainer,
            contentColor = Theme.colores.onSecondaryContainer,
            disabledContainerColor = Theme.colores.secondaryContainer.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onSecondaryContainer.copy(alpha = 0.50f)
        )

        ComponentVariant.Destructive -> ButtonDefaults.filledTonalButtonColors(
            containerColor = Theme.colores.errorContainer,
            contentColor = Theme.colores.onErrorContainer,
            disabledContainerColor = Theme.colores.errorContainer.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onErrorContainer.copy(alpha = 0.50f)
        )
    }


    MaterialFilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.buttonSize(size),
        shape = ButtonShape,
        colors = colores,
        contentPadding = when (size) {
            ComponentSize.Default -> Theme.dimens.paddingButton
            ComponentSize.Small -> Theme.dimens.paddingButtonSmall
        },
    ) {
        content()
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
private fun PrimaryTonalButtonPreview() {
    PagoFlexTheme {
        Surface {
            TonalButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Primary,
                size = ComponentSize.Default,
            ) {
                Text("Botón preview")
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
private fun SecondaryTonalButtonPreview() {
    PagoFlexTheme {
        Surface {
            TonalButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Secondary,
                size = ComponentSize.Default,
            ) {
                Text("Botón preview")
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
private fun DestructiveTonalButtonPreview() {
    PagoFlexTheme {
        Surface {
            TonalButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Destructive,
                size = ComponentSize.Default,
            ) {
                Text("Botón preview")
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
private fun PrimaryTonalButtonPreviewSmall() {
    PagoFlexTheme {
        Surface {
            TonalButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Primary,
                size = ComponentSize.Small,
            ) {
                Text("Botón preview")
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
private fun SecondaryTonalButtonPreviewSmall() {
    PagoFlexTheme {
        Surface {
            TonalButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Secondary,
                size = ComponentSize.Small,
            ) {
                Text("Botón preview")
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
private fun DestructiveTonalButtonPreviewSmall() {
    PagoFlexTheme {
        Surface {
            TonalButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Destructive,
                size = ComponentSize.Small,
            ) {
                Text("Botón preview")
            }
        }
    }
}