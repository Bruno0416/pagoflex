package com.pagoflex.app.features.cumplimiento.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.cumplimiento.data.CategoriaCumplimiento

@Composable
internal fun EscalaCumplimiento(categoria: CategoriaCumplimiento?) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)) {
        Text(
            stringResource(R.string.cumplimiento_escala_titulo),
            style = Theme.type.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        CategoriaFila(CategoriaCumplimiento.EJEMPLAR, categoria, R.string.cumplimiento_categoria_ejemplar, R.string.cumplimiento_rango_ejemplar)
        CategoriaFila(CategoriaCumplimiento.AL_DIA, categoria, R.string.cumplimiento_categoria_al_dia, R.string.cumplimiento_rango_al_dia)
        CategoriaFila(CategoriaCumplimiento.EN_CAMINO, categoria, R.string.cumplimiento_categoria_en_camino, R.string.cumplimiento_rango_en_camino)
    }
}

// --- fila de categoria ---
@Composable
private fun CategoriaFila(
    valor: CategoriaCumplimiento,
    seleccionada: CategoriaCumplimiento?,
    nombreRes: Int,
    rangoRes: Int
) {
    val activa = valor == seleccionada
    Surface(
        color = if (activa) Theme.colores.primaryContainer else Theme.colores.background,
        contentColor = if (activa) Theme.colores.onPrimaryContainer else Theme.colores.onSurface,
        shape = RoundedCornerShape(Theme.dimens.radiusS),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Theme.dimens.spacingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
        ) {
            Text(
                text = stringResource(nombreRes),
                modifier = Modifier.weight(1f),
                style = Theme.type.bodyLarge,
                fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(text = stringResource(rangoRes), style = Theme.type.bodyMedium)
            if (activa) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null
                )
            }
        }
    }
}
