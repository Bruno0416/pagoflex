package com.pagoflex.app.features.historial.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.containers.Divider
import com.pagoflex.app.core.ui.theme.Theme
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

// ---------- datos de comprobante ----------
@Composable
fun DatosComprobante(comprobante: Comprobante) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colores.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, Theme.colores.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            FilaComprobante(
                stringResource(R.string.comprobante_numero),
                comprobante.numeroComprobante,
                true
            )
            FilaComprobante(
                stringResource(R.string.comprobante_fecha_hora),
                comprobante.fechaHora.format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                )
            )
            FilaComprobante(
                stringResource(R.string.comprobante_pagado_por),
                "${comprobante.nombrePagador} · ${comprobante.rut.ocultarRut()}"
            )
            FilaComprobante(stringResource(R.string.comprobante_empresa), comprobante.empresa)
            FilaComprobante(stringResource(R.string.comprobante_folio), comprobante.folio)
            val cuota =
                if (comprobante.numeroCuota != null && comprobante.totalCuotas != null) {
                    " · ${comprobante.numeroCuota}/${comprobante.totalCuotas}"
                } else ""
            FilaComprobante(
                stringResource(R.string.comprobante_concepto),
                comprobante.concepto + cuota
            )
            FilaComprobante(
                stringResource(R.string.comprobante_vencimiento),
                comprobante.fechaVencimiento.format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
                )
            )
            Divider()
            FilaComprobante(
                stringResource(R.string.comprobante_monto_cuota),
                comprobante.montoCuota.toLong().formatoCLP()
            )
            FilaComprobante(
                stringResource(R.string.comprobante_recargo),
                comprobante.recargo.toLong().formatoCLP()
            )
            FilaComprobante(
                stringResource(R.string.comprobante_total_pagado),
                comprobante.montoPagado.toLong().formatoCLP(),
                true
            )
            Divider()
            FilaComprobante(stringResource(R.string.comprobante_medio_pago), comprobante.medioPago)
            FilaComprobante(stringResource(R.string.comprobante_canal), comprobante.canal)
        }
    }
}

// --- formatear monto en CLP ---
fun Long.formatoCLP(): String = "$ " +
        NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL")).format(this)

// --- ocultar RUT ---
fun String.ocultarRut(): String {
    val limpio = filter { it.isDigit() || it.equals('k', true) }
    if (limpio.length < 4) return "•••"
    val cuerpo = limpio.dropLast(1)
    val oculto = "•".repeat(cuerpo.length - 3) + cuerpo.takeLast(3)
    val formateado = oculto.reversed().chunked(3).joinToString(".").reversed()
    return "$formateado-${limpio.last()}"
}
