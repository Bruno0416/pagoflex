package com.pagoflex.app.features.home.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.ChipColors
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.model.CompromisoInicio
import com.pagoflex.app.features.home.model.EstadoCompromiso
import com.pagoflex.app.features.home.model.ResumenInicio
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
        val mes = vencidoDestacado.fechaVencimiento.month.getDisplayName(
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
                    "Te falta $cantidadVencidos pago para quedar al dia",
                    style = Theme.type.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(
                    Modifier.size(Theme.dimens.spacingXXS)
                )
                Text(
                    "Cuota de $mes del ${vencidoDestacado.organizacion}",
                    style = Theme.type.bodyMedium
                )

            }
        )
    }
}

// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun AvisoVencimientoPreview() {

    // Compromiso vencido ficticio
    val compromisoVencido = CompromisoInicio(
        id = "comp-001",
        organizacion = "Club Los Halcones",
        siglaOrganizacion = "CH",
        concepto = "Cuota de octubre del",
        numeroCuota = 10,
        totalCuotas = 12,
        fechaVencimiento = LocalDate.of(2026, 10, 5),
        monto = 17_000L,
        estado = EstadoCompromiso.VENCIDO
    )

    // Resumen ficticio
    val resumen = ResumenInicio(
        totalPorPagar = 633_400L,
        totalRecargos = 0L,
        cantidadCompromisos = 4,
        cantidadOrganizaciones = 4,
        cantidadVencidos = 1,
        porcentajeCumplimiento = 91.7,
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
