package com.pagoflex.app.core.ui.components.navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

// barra superior de las pantallas de agente, ejecutivo y detalles
@Composable
fun BarraSuperior(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.spacingXS, vertical = Theme.dimens.spacingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onVolver != null) {
            IconButton(
                onClick = onVolver,
                iconRes = R.drawable.ic_arrow_back,
                description = stringResource(R.string.ui_volver),
            )
        } else {
            Spacer(Modifier.width(Theme.dimens.spacingS))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = Theme.type.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant,
                )
            }
        }

        acciones()
    }
}

// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun BarraSuperiorPreview() {
    PagoFlexTheme {
        Surface {
            BarraSuperior(
                titulo = "Cobranza",
                subtitulo = "Altos del Maipo",
                onVolver = {},
            )
        }
    }
}
