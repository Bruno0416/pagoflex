package com.pagoflex.app.features.detalle.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.detalle.data.DetalleCompromisoRow
import java.time.format.DateTimeFormatter

@Composable
fun InformacionDetalle(detalle: DetalleCompromisoRow) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        variant = ComponentVariant.Primary
    ) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(
                Theme.dimens.spacingM
            )
        ) {

            FilaDetalle(
                etiqueta = stringResource(R.string.detalle_vencimiento),
                valor = detalle.fechaVencimiento.format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
                )
            )

            FilaDetalle(
                etiqueta = stringResource(R.string.detalle_folio),
                valor = detalle.folio
            )

            FilaDetalle(
                etiqueta = stringResource(R.string.detalle_contacto),
                valor = detalle.correoContacto
            )
        }
    }
}
