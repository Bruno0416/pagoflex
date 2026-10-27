package com.pagoflex.app.core.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Button as MaterialButton


@Composable
fun Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    size: ComponentSize = ComponentSize.Default,
    content: @Composable RowScope.() -> Unit,
) {
    val colores = when (variant) {
        ComponentVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = Theme.colores.primary,
            contentColor = Theme.colores.onPrimary,
            disabledContainerColor = Theme.colores.primary.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onPrimary.copy(alpha = 0.50f)
        )

        ComponentVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = Theme.colores.secondary,
            contentColor = Theme.colores.onSecondary,
            disabledContainerColor = Theme.colores.secondary.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onSecondary.copy(alpha = 0.50f)
        )

        ComponentVariant.Destructive -> ButtonDefaults.buttonColors(
            containerColor = Theme.colores.error,
            contentColor = Theme.colores.onError,
            disabledContainerColor = Theme.colores.error.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onPrimary.copy(alpha = 0.50f)
        )
    }


    MaterialButton(
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
private fun PrimaryButtonPreview() {
    PagoFlexTheme {
        Surface {
            Button(
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
private fun SecondaryButtonPreview() {
    PagoFlexTheme {
        Surface {
            Button(
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
private fun DestructiveButtonPreview() {
    PagoFlexTheme {
        Surface {
            Button(
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
private fun PrimaryButtonPreviewSmall() {
    PagoFlexTheme {
        Surface {
            Button(
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
private fun SecondaryButtonPreviewSmall() {
    PagoFlexTheme {
        Surface {
            Button(
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
private fun DestructiveButtonPreviewSmall() {
    PagoFlexTheme {
        Surface {
            Button(
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