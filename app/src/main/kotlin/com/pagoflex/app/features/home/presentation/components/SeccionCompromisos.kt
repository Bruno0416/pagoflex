package com.pagoflex.app.features.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.TextButton
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun SeccionCompromisos(
    modifier: Modifier = Modifier,
    onVerTodos: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues = Theme.dimens.paddingSides),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.home_tus_compromisos),
            style = Theme.type.titleMedium.copy(color = Theme.colores.onSurface)
        )
        Spacer(Modifier.weight(1F))
        TextButton(onClick = onVerTodos, size = ComponentSize.Small) {
            Text(stringResource(R.string.home_ver_todos))
        }
    }
}

// ---------- preview ----------

@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun SeccionCompromisosPreview() {
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Primary,
            shape = RoundedCornerShape(0)
        ) {
            SeccionCompromisos(modifier = Modifier.padding(16.dp), onVerTodos = {})

        }
    }
}
