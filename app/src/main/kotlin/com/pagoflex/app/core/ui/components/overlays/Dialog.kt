package com.pagoflex.app.core.ui.components.overlays

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    variant: ComponentVariant = ComponentVariant.Primary,
) {
    val confirmColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.secondary
        ComponentVariant.Destructive -> Theme.colores.error
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = confirmColor),
            ) {
                Text(confirmText)
            }
        },
        dismissButton = if (dismissText != null && onDismiss != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        } else {
            null
        },
    )
}
