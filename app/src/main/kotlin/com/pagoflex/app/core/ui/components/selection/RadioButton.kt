package com.pagoflex.app.core.ui.components.selection

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton as MaterialRadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ComponentVariant = ComponentVariant.Primary,
    label: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val selectedColor = when (variant) {
        ComponentVariant.Primary -> Theme.colores.primary
        ComponentVariant.Secondary -> Theme.colores.secondary
        ComponentVariant.Destructive -> Theme.colores.error
    }

    Row(
        modifier = modifier.selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
    ) {
        MaterialRadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = selectedColor,
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
private fun RadioButtonPreview() {
    PagoFlexTheme {
        Surface {
            RadioButton(
                modifier = Modifier.padding(16.dp),
                selected = true,
                onClick = {},
                label = "Opción seleccionada",
            )
        }
    }
}
