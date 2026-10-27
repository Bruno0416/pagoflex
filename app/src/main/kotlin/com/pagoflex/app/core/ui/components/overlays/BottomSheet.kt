package com.pagoflex.app.core.ui.components.overlays

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pagoflex.app.core.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    gesturesEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetGesturesEnabled = gesturesEnabled,
        containerColor = Theme.colores.surfaceContainerLow,
        contentColor = Theme.colores.onSurface,
        content = content,
    )
}