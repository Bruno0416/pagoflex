package com.pagoflex.app.core.ui.components.feedback

import androidx.compose.runtime.Composable
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun progressColors(variant: ComponentVariant) = when (variant) {
    ComponentVariant.Primary -> Theme.colores.primary to Theme.colores.primaryContainer
    ComponentVariant.Secondary -> Theme.colores.secondary to Theme.colores.secondaryContainer
    ComponentVariant.Destructive -> Theme.colores.error to Theme.colores.errorContainer
}
