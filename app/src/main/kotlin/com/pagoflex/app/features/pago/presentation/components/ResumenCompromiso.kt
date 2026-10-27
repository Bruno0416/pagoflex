package com.pagoflex.app.features.pago.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.pago.data.ResumenPago
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun ResumenCompromiso(resumen: ResumenPago) {
    val cuota = if (resumen.numeroCuota != null && resumen.totalCuotas != null) {
        stringResource(R.string.pago_resumen_cuota, resumen.numeroCuota, resumen.totalCuotas)
    } else ""

    Card(modifier = Modifier.fillMaxWidth(), variant = ComponentVariant.Primary) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
        ) {
            Text(
                text = stringResource(
                    R.string.pago_resumen_concepto,
                    resumen.organizacion,
                    resumen.concepto
                ) + cuota,
                style = Theme.type.bodyMedium,
                color = Theme.colores.onSurfaceVariant
            )
            Text(
                text = resumen.folio,
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )
            Text(
                text = resumen.total.formatoCLP(),
                style = Theme.type.headlineMedium,
                color = Theme.colores.onSurface
            )
            Text(
                text = if (resumen.recargo > 0) {
                    stringResource(
                        R.string.pago_resumen_con_recargo,
                        resumen.monto.formatoCLP(),
                        resumen.recargo.formatoCLP(),
                        resumen.total.formatoCLP()
                    )
                } else {
                    stringResource(R.string.pago_resumen_sin_recargo, resumen.monto.formatoCLP())
                },
                style = Theme.type.bodyMedium,
                color = Theme.colores.onSurfaceVariant
            )
        }
    }
}

// --- formato CLP ---
private fun Number.formatoCLP(): String =
    "$ " + NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CL")).format(toLong())

// ---------- previews ----------
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResumenCompromisoPreview() {
    PagoFlexTheme {
        ResumenCompromiso(
            ResumenPago(
                folio = "CP-2026-0102",
                organizacion = "Club Los Halcones",
                concepto = "Cuota social octubre",
                numeroCuota = 10,
                totalCuotas = 12,
                monto = 15_000,
                recargo = 2_000,
                total = 17_000,
                fechaVencimiento = LocalDate.of(2026, 10, 5),
                fechaConsulta = LocalDate.of(2026, 10, 25)
            )
        )
    }
}
