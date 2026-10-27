package com.pagoflex.app.features.detalle.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun AvisoAtraso() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colores.primary.copy(alpha = 0.12f),
            contentColor = Theme.colores.primary
        )
    ) {
        Row(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                tint = Theme.colores.primary,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(R.string.detalle_aviso_recargo),
                style = Theme.type.bodyMedium,
                color = Theme.colores.primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
