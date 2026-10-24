package com.pagoflex.app.features.historial.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.historial.data.HistorialRepository
import com.pagoflex.app.features.historial.presentation.components.CabeceraComprobante
import com.pagoflex.app.features.historial.presentation.components.DatosComprobante
import com.pagoflex.app.features.historial.presentation.model.ComprobanteUiState
import com.pagoflex.app.features.historial.presentation.viewmodel.ComprobanteViewModel
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ComprobanteScreen(
    db: LocalDatabase,
    usuarioId: Int,
    numeroComprobante: String,
    onCerrar: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { HistorialRepository(db) }
    val viewModel: ComprobanteViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ComprobanteViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ---------- cargar comprobante ----------
    LaunchedEffect(numeroComprobante, usuarioId) {
        viewModel.cargarComprobante(numeroComprobante, usuarioId)
    }

    ComprobanteContent(
        uiState = uiState,
        onCerrar = onCerrar
    )
}

@Composable
private fun ComprobanteContent(
    uiState: ComprobanteUiState,
    onCerrar: () -> Unit
) {
    val comprobante = uiState.comprobante

    Scaffold(
        // ---------- appbar superior ----------
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.comprobante_titulo), style = Theme.type.titleLarge)
                },
                navigationIcon = {
                    IconButton(
                        variant = ComponentVariant.Secondary,
                        onClick = onCerrar,
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.comprobante_cerrar)
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Theme.colores.background
                )
            )
        }
    ) { padding ->
        when {
            uiState.cargando -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.mensajeError != null || comprobante == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.mensajeError?.let { stringResource(it) }
                        ?: stringResource(R.string.comprobante_no_encontrado),
                    modifier = Modifier.padding(Theme.dimens.spacingL),
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(Theme.dimens.paddingSides),
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)
                ) {
                    CabeceraComprobante(comprobante)
                    DatosComprobante(comprobante)
                }
            }
        }
    }
}

// ---------- previews ----------
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ComprobantePreview() {
    val ejemplo = Comprobante(
        numeroComprobante = "CPR-000451",
        idPago = 1,
        idUsuarioFinal = 1,
        fechaHora = LocalDateTime.of(2026, 9, 24, 10, 42),
        nombrePagador = "Camila Rojas",
        rut = "15.111.111-3",
        empresa = "Crédito Andino",
        folio = "CP-2026-0104",
        concepto = "Crédito de consumo",
        numeroCuota = 7,
        totalCuotas = 24,
        fechaVencimiento = LocalDate.of(2026, 9, 25),
        montoCuota = 86_400,
        recargo = 0,
        montoPagado = 86_400,
        medioPago = "Tarjeta de débito",
        canal = "Portal de pagos"
    )
    PagoFlexTheme {
        ComprobanteContent(
            uiState = ComprobanteUiState(comprobante = ejemplo, cargando = false),
            onCerrar = {}
        )
    }
}
