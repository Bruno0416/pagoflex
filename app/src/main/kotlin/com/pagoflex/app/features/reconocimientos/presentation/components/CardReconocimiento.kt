package com.pagoflex.app.features.reconocimientos.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.reconocimientos.data.ReconocimientoObtenidoRow
import java.time.format.DateTimeFormatter

@Composable
internal fun CardReconocimiento(reconocimiento: ReconocimientoObtenidoRow) {
    val oscuro = isSystemInDarkTheme()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Theme.formas.large,
        color = if (oscuro) Theme.colores.surfaceContainerHighest else Theme.colores.surfaceContainerLowest,
        shadowElevation = if (oscuro) 0.dp else 3.dp
    ) {
        Row(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_workspace_premium),
                contentDescription = null,
                tint = Theme.colores.primary
            )
            Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)) {
                Text(
                    reconocimiento.nombre,
                    style = Theme.type.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(reconocimiento.descripcion, style = Theme.type.bodyMedium)
                Text(reconocimiento.motivo, style = Theme.type.bodyMedium, color = Theme.colores.onSurfaceVariant)
                Text(
                    stringResource(
                        R.string.reconocimientos_fecha,
                        reconocimiento.fechaObtencion.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    ),
                    style = Theme.type.bodyMedium,
                    color = Theme.colores.primary
                )
            }
        }
    }
}
