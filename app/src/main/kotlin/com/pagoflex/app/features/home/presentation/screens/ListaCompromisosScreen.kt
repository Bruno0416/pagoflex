package com.pagoflex.app.features.home.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.pagoflex.app.core.ui.components.feedback.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.home.presentation.components.CardCompromiso
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.home.presentation.model.ListaCompromisosUiState
import com.pagoflex.app.features.home.presentation.viewmodel.ListaCompromisosViewModel
import java.time.LocalDate

@Composable
fun ListaCompromisosScreen(
    onVolver: () -> Unit,
    onVerCompromiso: (String) -> Unit
) {
    // ---------- dependencias ----------
    val repositorio = remember { HomeRepository() }
    val viewModel: ListaCompromisosViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ListaCompromisosViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. recargar los compromisos al volver a la lista
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarCompromisos()
    }

    // ---------- contenido ----------
    ListaCompromisosContent(
        uiState = uiState,
        onVolver = onVolver,
        onVerCompromiso = onVerCompromiso
    )
}

@Composable
private fun ListaCompromisosContent(
    uiState: ListaCompromisosUiState,
    onVolver: () -> Unit,
    onVerCompromiso: (String) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.home_lista_titulo),
                        style = Theme.type.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.home_lista_volver)
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Theme.colores.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(Theme.dimens.paddingSides),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.cargando -> CircularProgressIndicator()

                uiState.mensajeError != null -> {
                    Text(stringResource(uiState.mensajeError))
                }

                uiState.compromisos.isEmpty() -> {
                    Text(stringResource(R.string.home_lista_vacia))
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
                ) {
                    uiState.compromisos.forEach { compromiso ->
                        CardCompromiso(
                            compromiso = compromiso,
                            onClick = { onVerCompromiso(compromiso.folio) }
                        )
                    }
                }
            }
        }
    }
}

// ---------- previews ----------

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun ListaCompromisosPreview() {
    val compromisos = listOf(
        MiCompromisoResponse(
        folio = "CP-2026-0102",
        empresaCodigo = null,
        empresa = "Club Los Halcones",
        correoAtencion = null,
        concepto = "Cuota social octubre",
        numeroCuota = 10,
        totalCuotas = 12,
        monto = 15000L,
        recargo = 2000L,
        total = 17000L,
        fechaVencimiento = "2026-10-05",
        estado = "VENCIDO",
        diasAtraso = 0L,
        pagable = true,
        folioAnterior = null,
        numeroComprobante = null,
        fechaPago = null
    ),
        MiCompromisoResponse(
        folio = "CP-2026-0105",
        empresaCodigo = null,
        empresa = "Crédito Andino",
        correoAtencion = null,
        concepto = "Crédito de consumo",
        numeroCuota = 8,
        totalCuotas = 24,
        monto = 86400L,
        recargo = 0L,
        total = 86400L,
        fechaVencimiento = "2026-10-25",
        estado = "PENDIENTE",
        diasAtraso = 0L,
        pagable = true,
        folioAnterior = null,
        numeroComprobante = null,
        fechaPago = null
    ),
        MiCompromisoResponse(
        folio = "CP-2026-0104",
        empresaCodigo = null,
        empresa = "Crédito Andino",
        correoAtencion = null,
        concepto = "Crédito de consumo",
        numeroCuota = 7,
        totalCuotas = 24,
        monto = 86400L,
        recargo = 0L,
        total = 86400L,
        fechaVencimiento = "2026-09-25",
        estado = "PAGADO",
        diasAtraso = 0L,
        pagable = true,
        folioAnterior = null,
        numeroComprobante = null,
        fechaPago = null
    )
    )

    PagoFlexTheme {
        ListaCompromisosContent(
            uiState = ListaCompromisosUiState(
                compromisos = compromisos,
                cargando = false
            ),
            onVolver = {},
            onVerCompromiso = {}
        )
    }
}
