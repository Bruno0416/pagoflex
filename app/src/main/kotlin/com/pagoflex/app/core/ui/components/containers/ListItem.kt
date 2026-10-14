package com.pagoflex.app.core.ui.components.containers

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import androidx.compose.material3.ListItem as MaterialListItem


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    headlineContent: @Composable () -> Unit,
    variant: ComponentVariant = ComponentVariant.Primary,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val containerColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.surfaceContainer
        ComponentVariant.Secondary -> Theme.colores.surfaceContainerHigh
        ComponentVariant.Destructive -> Theme.colores.errorContainer
    }

    val contentColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.onSurface
        ComponentVariant.Secondary -> Theme.colores.onSurface
        ComponentVariant.Destructive -> Theme.colores.onErrorContainer
    }

    MaterialListItem(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            supportingColor = contentColor.copy(alpha = 0.72f),
            leadingIconColor = contentColor,
            trailingIconColor = contentColor,
        ),
    )
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
private fun ListItemPreview() {
    PagoFlexTheme {
        Surface {
            ListItem(
                modifier = Modifier.padding(16.dp),
                headlineContent = { Text("Pago de tarjeta") },
                supportingContent = { Text("Vence el 20 de octubre") },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_credit_card),
                        contentDescription = null,
                    )
                },
                trailingContent = { Text("$ 86.400") },
            )
        }
    }
}
