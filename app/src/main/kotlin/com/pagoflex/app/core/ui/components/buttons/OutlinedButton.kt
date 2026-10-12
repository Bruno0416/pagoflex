package com.pagoflex.app.core.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.model.buttonSize
import com.pagoflex.app.core.ui.theme.ButtonShape
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.OutlinedButton as MaterialOutlinedButton


@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    size: ComponentSize = ComponentSize.Default,
    content: @Composable RowScope.() -> Unit,
) {
    val colorContenido = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.onSurface
        ComponentVariant.Destructive -> Theme.colores.error
    }

    val borderColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.outline
        ComponentVariant.Destructive -> Theme.colores.error
    }


    MaterialOutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.buttonSize(size),
        shape = ButtonShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = colorContenido,
            disabledContainerColor = Theme.colores.surfaceContainerHighest,
            disabledContentColor =
                Theme.colores.outline

        ),
        border = BorderStroke(
            width = if (
                size == ComponentSize.Small
            ) {
                1.5.dp
            } else {
                2.dp
            },
            color = borderColor,
        ),
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
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PrimaryOutlinedButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            OutlinedButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Primary,
                size = ComponentSize.Default,
                ){
                Text("Botón preview")
            }
        }

    }


}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SecondaryOutlinedButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            OutlinedButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Secondary,
                size = ComponentSize.Default,
                ){
                Text("Botón preview")
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun DestructiveOutlinedButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            OutlinedButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Destructive,
                size = ComponentSize.Default,
                ){
                Text("Botón preview")
            }
        }
    }
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PrimaryOutlinedButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            OutlinedButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Primary,
                size = ComponentSize.Small,
                ){
                Text("Botón preview")
            }
        }
    }

}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SecondaryOutlinedButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            OutlinedButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Secondary,
                size = ComponentSize.Small,
                ){
                Text("Botón preview")
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun DestructiveOutlinedButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            OutlinedButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Destructive,
                size = ComponentSize.Small,
            ){
                Text("Botón preview")
            }
        }
    }
}