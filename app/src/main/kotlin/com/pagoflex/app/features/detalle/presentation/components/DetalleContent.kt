package com.pagoflex.app.features.detalle.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.detalle.presentation.model.DetalleUiState
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun DetalleContent(
    uiState: DetalleUiState,
    onVolver: () -> Unit,
    onPagar: (() -> Unit)?,
    onReportar: (() -> Unit)?
) {
    val detalle = uiState.detalle ?: return

    // ---------- estado y colores ----------
    val estado = EstadoCompromiso.entries.firstOrNull { it.name == detalle.estado }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        // ---------- appbar ----------
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.detalle_titulo),
                        style = Theme.type.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        variant = ComponentVariant.Secondary,
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.detalle_volver)
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Theme.colores.background
                )
            )
        },

        // ---------- acciones ----------
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimens.paddingSides)
                    .padding(vertical = Theme.dimens.spacingM),
                verticalArrangement = Arrangement.spacedBy(
                    Theme.dimens.spacingM
                )
            ) {
                if (uiState.puedeIniciarPago) {
                    Button(
                        onClick = { onPagar?.invoke() },
                        enabled = onPagar != null
                    ) {
                        Text(
                            stringResource(
                                R.string.detalle_pagar,
                                detalle.total.formatoCLP()
                            )
                        )
                    }
                } else if (detalle.estado == "EN_REVISION") {
                    Text(
                        text = stringResource(R.string.pago_no_pagable_revision),
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.onSurfaceVariant
                    )
                } else if (detalle.estado == "ANULADO") {
                    Text(
                        text = stringResource(R.string.pago_no_pagable_anulado),
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { onReportar?.invoke() },
                    enabled = onReportar != null
                ) {
                    Text(stringResource(R.string.detalle_reportar))
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Theme.dimens.paddingSides),
            verticalArrangement = Arrangement.spacedBy(
                Theme.dimens.spacingL
            )
        ) {
            ResumenDetalle(detalle, estado)
            InformacionDetalle(detalle)
            if (estado == EstadoCompromiso.VENCIDO) {
                AvisoAtraso()
            }

            Spacer(Modifier.size(Theme.dimens.spacingM))
        }
    }
}

// --- formatear monto en clp ---
private fun Long.formatoCLP(): String {
    val formato = NumberFormat.getNumberInstance(
        Locale.forLanguageTag("es-CL")
    )
    return "$ ${formato.format(this)}"
}

// ---------- previews ----------
@Preview(showBackground = true, showSystemUi = true)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DetalleCompromisoPreview() {
    val ejemplo = MiCompromisoResponse(
        folio = "CP-2026-0102",
        empresaCodigo = null,
        empresa = "Club Los Halcones",
        correoAtencion = "tesoreria@loshalcones.ejemplo",
        concepto = "Cuota social octubre",
        numeroCuota = 10,
        totalCuotas = 12,
        monto = 15000L,
        recargo = 2000L,
        total = 17000L,
        fechaVencimiento = "2026-10-05",
        estado = "VENCIDO",
        diasAtraso = 17L,
        pagable = true,
        folioAnterior = null,
        numeroComprobante = null,
        fechaPago = null
    )

    PagoFlexTheme {
        Box(Modifier.statusBarsPadding()) {
            DetalleContent(
                uiState = DetalleUiState(
                    cargando = false,
                    detalle = ejemplo,
                ),
                onVolver = {},
                onPagar = {},
                onReportar = {}
            )
        }
    }
}
