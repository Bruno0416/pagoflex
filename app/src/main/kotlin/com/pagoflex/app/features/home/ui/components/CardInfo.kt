package com.pagoflex.app.features.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.theme.CardShapeMedium
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun CardInfo(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CardShapeMedium,
    icono: Painter,
    contenido: @Composable ColumnScope.() -> Unit,
    colores: CardColors,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = shape,
        colors = colores,
    ) {
        Row(
            modifier = Modifier.padding(Theme.dimens.spacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icono,
                contentDescription = null
            )
            Spacer(
                Modifier.size(Theme.dimens.spacingXS)
            )

            Column {
                contenido()
            }

            Spacer(
                Modifier.weight(1f)
            )

            if (onClick != null) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null
                )
            }
        }
    }
}