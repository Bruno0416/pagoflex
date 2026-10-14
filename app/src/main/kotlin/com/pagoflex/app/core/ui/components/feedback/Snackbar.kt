package com.pagoflex.app.core.ui.components.feedback

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Snackbar as MaterialSnackbar
import androidx.compose.material3.SnackbarHost as MaterialSnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun Snackbar(
    message: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant = ComponentVariant.Primary,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val containerColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.inverseSurface
        ComponentVariant.Secondary -> Theme.colores.secondaryContainer
        ComponentVariant.Destructive -> Theme.colores.errorContainer
    }

    val contentColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.inverseOnSurface
        ComponentVariant.Secondary -> Theme.colores.onSecondaryContainer
        ComponentVariant.Destructive -> Theme.colores.onErrorContainer
    }

    val actionColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.inversePrimary
        ComponentVariant.Secondary -> Theme.colores.secondary
        ComponentVariant.Destructive -> Theme.colores.error
    }

    MaterialSnackbar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionColor,
        action = if (actionLabel != null && onAction != null) {
            {
                TextButton(
                    onClick = onAction,
                    colors = ButtonDefaults.textButtonColors(contentColor = actionColor),
                ) {
                    Text(actionLabel)
                }
            }
        } else {
            null
        },
    ) {
        Text(message)
    }
}


@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    variant: ComponentVariant = ComponentVariant.Primary,
) {
    MaterialSnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        Snackbar(
            message = data.visuals.message,
            variant = variant,
            actionLabel = data.visuals.actionLabel,
            onAction = data.visuals.actionLabel?.let { { data.performAction() } },
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
private fun SnackbarPreview() {
    PagoFlexTheme {
        Surface {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                message = "Los cambios se guardaron correctamente",
                actionLabel = "Deshacer",
                onAction = {},
            )
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
private fun DestructiveSnackbarPreview() {
    PagoFlexTheme {
        Surface {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                message = "No fue posible completar la operación",
                variant = ComponentVariant.Destructive,
            )
        }
    }
}
