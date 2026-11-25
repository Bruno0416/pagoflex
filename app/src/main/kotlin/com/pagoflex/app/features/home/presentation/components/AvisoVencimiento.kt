package com.pagoflex.app.features.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
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
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.ChipColors
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.home.presentation.model.ResumenInicio
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AvisoVencimiento(
    modifier: Modifier = Modifier,
    resumen: ResumenInicio,
    onClick: () -> Unit,
) {
    val cantidadVencidos = resumen.cantidadVencidos
    val vencidoDestacado = resumen.vencidoDestacado



    if (cantidadVencidos > 0 && vencidoDestacado != null) {
        val mes = LocalDate.parse(vencidoDestacado.fechaVencimiento).month.getDisplayName(
            TextStyle.FULL,
            Locale.forLanguageTag("es-CL")
        )

        CardInfo(
            modifier = modifier,
            onClick = onClick,
            colores = CardDefaults.cardColors(
                containerColor = ChipColors.expiradoContainer,
                contentColor = ChipColors.expiradoContent,
            ),

            icono = painterResource(R.drawable.ic_priority_high),

            contenido = {
                Text(
                    pluralStringResource(
                        R.plurals.home_aviso_pagos_vencidos,
                        cantidadVencidos,
                        cantidadVencidos
                    ),
                    style = Theme.type.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(
                    Modifier.size(Theme.dimens.spacingXXS)
                )
                Text(
                    stringResource(
                        R.string.home_aviso_cuota,
                        mes,
                        vencidoDestacado.empresa
                    ),
                    style = Theme.type.bodyMedium
                )

            }
        )
    }
}

// ---------- previews ----------

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun AvisoVencimientoPreview() {

    // 1. preparar un compromiso vencido de ejemplo
    val compromisoVencido = MiCompromisoResponse(
        folio = "comp-001",
        empresaCodigo = null,
        empresa = "Club Los Halcones",
        correoAtencion = null,
        concepto = "Cuota de octubre del",
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

    // 2. preparar el resumen de ejemplo
    val resumen = ResumenInicio(
        totalPorPagar = 633_400L,
        totalRecargos = 0L,
        cantidadCompromisos = 4,
        cantidadOrganizaciones = 4,
        cantidadVencidos = 1,
        proximoCompromiso = null,
        vencidoDestacado = compromisoVencido
    )

    PagoFlexTheme {
        Card(
            shape = RoundedCornerShape(0),
            variant = ComponentVariant.Primary
        ) {
            AvisoVencimiento(
                modifier = Modifier.padding(16.dp),
                resumen = resumen,
                onClick = {}
            )

        }
    }
}
