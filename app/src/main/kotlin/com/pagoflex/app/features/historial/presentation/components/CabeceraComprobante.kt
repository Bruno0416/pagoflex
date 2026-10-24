package com.pagoflex.app.features.historial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun CabeceraComprobante(comprobante: Comprobante) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Theme.colores.primary.copy(alpha = 0.12f),
                    androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Theme.colores.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            stringResource(R.string.comprobante_cabecera),
            style = Theme.type.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.comprobante_pago_registrado, comprobante.empresa),
            color = Theme.colores.onSurfaceVariant,
            style = Theme.type.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
