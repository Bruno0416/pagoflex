package com.pagoflex.app.features.cobranza.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.cobranza.presentation.model.CompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.model.PersonaCompromiso
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.LocalDate
import java.time.LocalDateTime

// fila del listado de compromisos de la empresa
@Composable
fun CardCompromisoEmpresa(
    compromiso: CompromisoEmpresa,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val concepto = compromiso.textoCuota?.let {
        stringResource(R.string.cob_concepto_cuota, compromiso.concepto, it)
    } ?: compromiso.concepto

    Card(variant = ComponentVariant.Primary, modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = compromiso.persona.nombre,
                    style = Theme.type.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(text = concepto, style = Theme.type.bodySmall, color = Theme.colores.onSurfaceVariant)
                Text(
                    text = stringResource(R.string.cob_folio_vence, compromiso.folio, compromiso.fechaVencimiento.aTexto()),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant,
                )
            }

            Spacer(Modifier.width(Theme.dimens.spacingS))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXS),
            ) {
                Text(
                    text = compromiso.total.aPesos(),
                    style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                StatusChip(
                    label = stringResource(compromiso.estado.labelRes),
                    colores = compromiso.estado.coloresCard,
                )
            }
        }
    }
}

// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CardCompromisoEmpresaPreview() {
    PagoFlexTheme {
        Surface {
            CardCompromisoEmpresa(
                compromiso = CompromisoEmpresa(
                    folio = "CP-2026-0106",
                    concepto = "Cuota pie departamento",
                    numeroCuota = 14,
                    totalCuotas = 36,
                    monto = 310_000,
                    recargo = 8_500,
                    fechaVencimiento = LocalDate.of(2026, 9, 30),
                    estado = EstadoCompromiso.EN_REVISION,
                    diasAtraso = 6,
                    persona = PersonaCompromiso("Luis Alberto Muñoz Tapia", "12.476.318-5", "lmunoz@correo.ejemplo", null),
                    pago = null,
                    registradoPor = "Paula Andrea Saavedra Núñez",
                    fechaRegistro = LocalDateTime.of(2026, 8, 30, 9, 0),
                    anulacion = null,
                ),
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
