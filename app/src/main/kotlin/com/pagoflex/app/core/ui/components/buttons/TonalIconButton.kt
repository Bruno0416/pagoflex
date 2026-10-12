package com.pagoflex.app.core.ui.components.buttons

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.IconButtonShape
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.IconButton as MaterialIconButton


@Composable
fun TonalIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    size: ComponentSize = ComponentSize.Default,
    @DrawableRes iconRes: Int,
    description: String? = null
) {
    val colores = when (variant) {
        ComponentVariant.Primary -> IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = Theme.colores.primaryContainer,
            contentColor = Theme.colores.onPrimaryContainer,
            disabledContainerColor = Theme.colores.primaryContainer.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onPrimaryContainer.copy(alpha = 0.50f)
        )

        ComponentVariant.Secondary -> IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = Theme.colores.secondaryContainer,
            contentColor = Theme.colores.onSecondaryContainer,
            disabledContainerColor = Theme.colores.secondaryContainer.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onSecondaryContainer.copy(alpha = 0.50f)
        )

        ComponentVariant.Destructive -> IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = Theme.colores.errorContainer,
            contentColor = Theme.colores.onErrorContainer,
            disabledContainerColor = Theme.colores.errorContainer.copy(alpha = 0.20f),
            disabledContentColor = Theme.colores.onErrorContainer.copy(alpha = 0.50f)
        )
    }

    MaterialIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = if (size == ComponentSize.Small) {
            modifier
                .height(Theme.dimens.componentsHeightSmall)
                .width(Theme.dimens.componentsHeightSmall)
        } else {
            modifier
        },
        shape = IconButtonShape,
        colors = colores,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = description
        )
    }
}

// ----------------- PREVIEWS -----------------

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Botón primario Default"
)
@Composable
private fun PrimaryTonalIconButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            TonalIconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Primary,
                size = ComponentSize.Default,
                iconRes = R.drawable.ic_home,
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Botón secundario Default"
)
@Composable
private fun SecondaryTonalIconButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            TonalIconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Secondary,
                size = ComponentSize.Default,
                iconRes = R.drawable.ic_home,
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Botón destructivo Default"
)
@Composable
private fun DestructiveTonalIconButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            TonalIconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Destructive,
                size = ComponentSize.Default,
                iconRes = R.drawable.ic_home,
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Botón primario Small"
)
@Composable
private fun PrimaryTonalIconButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            TonalIconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Primary,
                size = ComponentSize.Small,
                iconRes = R.drawable.ic_home,
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Botón secundario Small"
)
@Composable
private fun SecondaryTonalIconButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            TonalIconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Secondary,
                size = ComponentSize.Small,
                iconRes = R.drawable.ic_home,
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Botón destructivo Small"
)
@Composable
private fun DestructiveTonalIconButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            TonalIconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {},
                enabled = true,
                variant = ComponentVariant.Destructive,
                size = ComponentSize.Small,
                iconRes = R.drawable.ic_home,
            )
        }
    }
}
