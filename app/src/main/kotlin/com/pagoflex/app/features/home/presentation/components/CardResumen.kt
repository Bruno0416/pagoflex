package com.pagoflex.app.features.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.CardShapeMedium
import com.pagoflex.app.core.ui.theme.Dimens.radiusM
import com.pagoflex.app.core.ui.theme.Dimens.spacingS
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.presentation.model.CompromisoInicio
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.home.presentation.model.ResumenInicio
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CardResumen(
    modifier: Modifier = Modifier,
    resumen: ResumenInicio,
    periodo: YearMonth
) {
    val nombreMes = periodo.month.getDisplayName(
        TextStyle.FULL,
        Locale.forLanguageTag("es-CL")
    )

    val proximoCompromiso = resumen.proximoCompromiso

    Card(
        modifier = modifier,
        shape = CardShapeMedium,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colores.primary,
            contentColor = Theme.colores.onPrimary
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(Theme.dimens.spacingM)
                .fillMaxWidth()
        ) {
            // ---- sección por pagar ----
            Text(
                stringResource(R.string.home_por_pagar_mes, nombreMes),
                style = Theme.type.bodyMedium
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingS)
            )

            Text(
                resumen.totalPorPagar.toCLP(),
                style = Theme.type.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingXS)
            )

            // ---- cantidades ----
            Text(
                stringResource(
                    R.string.home_separador_cantidades,
                    pluralStringResource(
                        R.plurals.home_cantidad_compromisos,
                        resumen.cantidadCompromisos,
                        resumen.cantidadCompromisos
                    ),
                    pluralStringResource(
                        R.plurals.home_cantidad_organizaciones,
                        resumen.cantidadOrganizaciones,
                        resumen.cantidadOrganizaciones
                    )
                ),
                style = Theme.type.bodySmall
            )

            Spacer(
                Modifier.size(Theme.dimens.spacingS)
            )

            // ---- proximo vencimiento ----
            proximoCompromiso?.let { compromiso ->
                CardInfo(
                    shape = RoundedCornerShape(radiusM - spacingS),
                    colores = CardDefaults.cardColors(
                        containerColor = Theme.colores.primaryContainer,
                        contentColor = Theme.colores.onPrimaryContainer
                    ),
                    icono = painterResource(R.drawable.ic_calendar_month),
                    contenido = {
                        Text(
                            stringResource(
                                R.string.home_proximo_vencimiento,
                                compromiso.fechaVencimiento.format("dd-MM-yyyy")
                            ),
                            style = Theme.type.bodyMedium
                        )
                        Spacer(
                            Modifier.size(Theme.dimens.spacingXXS)
                        )
                        Text(
                            stringResource(
                                R.string.home_proximo_detalle,
                                compromiso.concepto,
                                compromiso.organizacion,
                                compromiso.monto.toCLP()
                            ),
                            style = Theme.type.bodyMedium
                        )
                    }
                )
            }
        }
    }
}

// ---- helpers ----

private fun Long.toCLP(
    languageTag: String = "es-CL",
    currencySymbol: String = "$"
): String {
    val locale = Locale.forLanguageTag(languageTag)
    val formatter = NumberFormat.getNumberInstance(locale)
    return "$currencySymbol ${formatter.format(this)}"
}


private fun LocalDate.format(patron: String = "dd/MM"): String {
    return this.format(
        DateTimeFormatter.ofPattern(patron)
    )
}

// ----------------- PREVIEWS -----------------

@Preview(
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun CardResumenPreview() {

    // Próximo compromiso
    val proximoCompromiso = CompromisoInicio(
        id = "comp-002",
        organizacion = "Plaza Oriente",
        siglaOrganizacion = "PO",
        concepto = "Arriendo oct.",
        numeroCuota = 10,
        totalCuotas = 12,
        fechaVencimiento = LocalDate.of(2026, 10, 10),
        monto = 520_000L,
        estado = EstadoCompromiso.PENDIENTE
    )

    // Compromiso vencido
    val vencidoDestacado = CompromisoInicio(
        id = "comp-001",
        organizacion = "Club Los Halcones",
        siglaOrganizacion = "CH",
        concepto = "Cuota social oct.",
        numeroCuota = 10,
        totalCuotas = 12,
        fechaVencimiento = LocalDate.of(2026, 10, 5),
        monto = 17_000L,
        estado = EstadoCompromiso.VENCIDO
    )

    // Datos ficticios del resumen
    val resumen = ResumenInicio(
        totalPorPagar = 633_400L,
        totalRecargos = 0L,
        cantidadCompromisos = 4,
        cantidadOrganizaciones = 4,
        cantidadVencidos = 1,
        porcentajeCumplimiento = 91.7,
        proximoCompromiso = proximoCompromiso,
        vencidoDestacado = vencidoDestacado
    )

    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Primary,
            shape = RoundedCornerShape(0)
        ) {
            CardResumen(
                modifier = Modifier.padding(16.dp),
                resumen = resumen,
                periodo = YearMonth.of(2026, 10)
            )

        }
    }
}
