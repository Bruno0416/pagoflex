package com.pagoflex.app.core.ui.components.model

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pagoflex.app.core.ui.theme.Theme

enum class ComponentVariant {
    Primary,
    Secondary,
    Destructive
}

enum class ComponentSize {
    Default,
    Small
}


@Composable
fun Modifier.buttonSize(
    size: ComponentSize,
): Modifier {
    return when (size) {
        ComponentSize.Default -> this
            .fillMaxWidth()
            .height(Theme.dimens.componentsHeight)

        ComponentSize.Small -> this
            .height(Theme.dimens.componentsHeightSmall)
    }
}

