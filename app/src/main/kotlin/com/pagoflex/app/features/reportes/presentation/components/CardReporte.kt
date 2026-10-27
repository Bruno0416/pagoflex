package com.pagoflex.app.features.reportes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresAtrasado
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem

// fila de la bandeja de reportes del agente
@Composable
fun CardReporte(
    reporte: ReporteItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(variant = ComponentVariant.Primary, modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(
            modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXXS)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reporte.numero,
                    style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                if (reporte.atrasado) {
                    StatusChip(
                        label = stringResource(R.string.cat_reporte_atrasado),
                        colores = coloresAtrasado,
                        modifier = Modifier.padding(end = Theme.dimens.spacingXS)
                    )
                }
                StatusChip(label = stringResource(reporte.estado.labelRes), colores = reporte.estado.coloresCard)
            }
            Text(stringResource(reporte.motivo.labelRes), style = Theme.type.bodyMedium)
            Text(
                text = stringResource(R.string.rep_usuario_empresa, reporte.usuario, reporte.empresa),
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )
            Text(
                text = when (reporte.estado) {
                    EstadoReporte.ABIERTO -> stringResource(
                        R.string.rep_recibido_espera, reporte.fechaCreacion.aTexto(), reporte.diasHabiles
                    )

                    EstadoReporte.EN_GESTION -> stringResource(
                        R.string.rep_gestiona, reporte.agente.orEmpty(), reporte.fechaToma?.aTexto().orEmpty()
                    )

                    EstadoReporte.RESUELTO -> stringResource(
                        R.string.rep_resuelto_el, reporte.fechaResolucion?.aTexto().orEmpty()
                    )
                },
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )
        }
    }
}
