package com.pagoflex.app.features.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.avatar.Avatar
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CardCompromiso(
    modifier: Modifier = Modifier,
    compromiso: MiCompromisoResponse,
    onClick: () -> Unit
) {
    val detalleCuota = if (
        compromiso.numeroCuota != null &&
        compromiso.totalCuotas != null
    ) {
        stringResource(
            R.string.home_detalle_numero_cuota,
            compromiso.numeroCuota,
            compromiso.totalCuotas
        )
    } else {
        ""
    }
    val fechaVencimiento = LocalDate.parse(compromiso.fechaVencimiento).format()
    val estado = EstadoCompromiso.valueOf(compromiso.estado)
    val colores = estado.coloresCard

    Card(
        variant = ComponentVariant.Primary,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(Modifier.padding(Theme.dimens.paddingCardsCompromiso)) {
            Avatar(fallback = compromiso.empresa.split(' ').filter { it.isNotBlank() }
                .take(2).joinToString("") { it.take(1).uppercase() }, colores = colores)

            Spacer(
                Modifier.size(Theme.dimens.spacingS)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    compromiso.empresa,
                    style = Theme.type.titleMedium
                )
                Spacer(
                    Modifier.size(Theme.dimens.spacingXXS)
                )
                Text(
                    "${compromiso.concepto}$detalleCuota",
                    style = Theme.type.bodySmall
                        .copy(color = Theme.colores.onSurfaceVariant)
                )

                Text(
                    fechaVencimiento,
                    style = Theme.type.bodySmall
                        .copy(color = Theme.colores.onSurfaceVariant)
                )
            }

            Spacer(
                Modifier.size(Theme.dimens.spacingS)
            )

            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(
                    Theme.dimens.spacingXXS
                )
            ) {
                Text(
                    compromiso.total.toCLP(),
                    style = Theme.type.titleMedium
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(
                    Modifier.size(Theme.dimens.spacingXXS)
                )

                StatusChip(
                    label = stringResource(estado.labelRes),
                    colores = colores,

                    )
            }
        }

    }
}


// ---------- helpers ----------
private fun Long.toCLP(
    languageTag: String = "es-CL",
    currencySymbol: String = "$"
): String {
    val locale = Locale.forLanguageTag(languageTag)
    val formatter = NumberFormat.getNumberInstance(locale)
    return "$currencySymbol ${formatter.format(this)}"
}

private fun LocalDate.format(patron: String = "dd-MM-yyyy"): String {
    return this.format(
        DateTimeFormatter.ofPattern(patron)
    )
}


// ---------- previews ----------

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun CardCompromisoPreview() {

    // 1. preparar un compromiso vencido de ejemplo
    val compromisoVencido = MiCompromisoResponse(
        folio = "comp-001",
        empresaCodigo = null,
        empresa = "Club Los Halcones",
        correoAtencion = null,
        concepto = "Cuota social oct.",
        numeroCuota = 10,
        totalCuotas = 12,
        monto = 17000L,
        recargo = 0L,
        total = 17000L,
        fechaVencimiento = "2026-10-05",
        estado = "VENCIDO",
        diasAtraso = 0L,
        pagable = true,
        folioAnterior = null,
        numeroComprobante = null,
        fechaPago = null
    )

    // 2. preparar un compromiso pendiente de ejemplo
    val compromisoPendiente = MiCompromisoResponse(
        folio = "comp-002",
        empresaCodigo = null,
        empresa = "Plaza Oriente",
        correoAtencion = null,
        concepto = "Arriendo oct.",
        numeroCuota = 10,
        totalCuotas = 12,
        monto = 520000L,
        recargo = 0L,
        total = 520000L,
        fechaVencimiento = "2026-10-10",
        estado = "PENDIENTE",
        diasAtraso = 0L,
        pagable = true,
        folioAnterior = null,
        numeroComprobante = null,
        fechaPago = null
    )

    PagoFlexTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimens.spacingM),
                verticalArrangement = Arrangement.spacedBy(
                    Theme.dimens.spacingS
                )
            ) {
                CardCompromiso(
                    compromiso = compromisoVencido,
                    onClick = {}
                )

                CardCompromiso(
                    compromiso = compromisoPendiente,
                    onClick = {}
                )
            }
        }
    }
}
