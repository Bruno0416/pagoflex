package com.pagoflex.app.core.ui.components.selection

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox as MaterialCheckbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    label: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val checkedColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.secondary
        ComponentVariant.Destructive -> Theme.colores.error
    }

    val checkmarkColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.onPrimary
        ComponentVariant.Secondary -> Theme.colores.onSecondary
        ComponentVariant.Destructive -> Theme.colores.onError
    }

    Row(
        modifier = modifier.toggleable(
            value = checked,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
    ) {
        MaterialCheckbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = checkedColor,
                checkmarkColor = checkmarkColor,
            ),
        )

        if (label != null) {
            Text(
                text = label,
                style = Theme.type.bodyLarge,
            )
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
private fun CheckboxPreview() {
    var checked by remember { mutableStateOf(true) }

    PagoFlexTheme {
        Surface {
            Checkbox(
                modifier = Modifier.padding(16.dp),
                checked = checked,
                onCheckedChange = { checked = it },
                label = "Recordarme",
            )
        }
    }
}
