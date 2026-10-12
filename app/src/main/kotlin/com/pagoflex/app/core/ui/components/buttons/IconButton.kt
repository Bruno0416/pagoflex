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
import androidx.compose.ui.graphics.Color
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
fun IconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    size: ComponentSize = ComponentSize.Default,
    @DrawableRes iconRes: Int,
    description: String? = null
) {
    val contentColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.secondary
        ComponentVariant.Destructive -> Theme.colores.error
    }

    val colores = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = contentColor,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = contentColor.copy(alpha = 0.38f)
    )

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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PrimaryIconButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            IconButton(
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SecondaryIconButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            IconButton(
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DestructiveIconButtonPreview() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            IconButton(
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PrimaryIconButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            IconButton(
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SecondaryIconButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            IconButton(
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DestructiveIconButtonPreviewSmall() {
    PagoFlexTheme {
        Card(shape = RoundedCornerShape(0)) {
            IconButton(
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
