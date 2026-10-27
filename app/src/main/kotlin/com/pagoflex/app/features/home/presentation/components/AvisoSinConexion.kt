package com.pagoflex.app.features.home.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AvisoSinConexion(fecha: LocalDateTime?) {
    val texto = if (fecha != null) {
        stringResource(
            R.string.home_sin_conexion_ultima_carga,
            fecha.format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm")
            )
        )
    } else {
        stringResource(R.string.home_sin_conexion_sin_carga)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_wifi_off),
            contentDescription = null,
            tint = Theme.colores.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )

        Spacer(Modifier.size(Theme.dimens.spacingXS))

        Text(
            text = texto,
            style = Theme.type.bodySmall,
            color = Theme.colores.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
