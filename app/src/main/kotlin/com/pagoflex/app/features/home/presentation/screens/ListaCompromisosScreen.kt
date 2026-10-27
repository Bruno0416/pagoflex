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
import androidx.compose.material3.CircularProgressIndicator
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
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.home.presentation.components.CardCompromiso
import com.pagoflex.app.features.home.presentation.model.CompromisoInicio
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.home.presentation.model.ListaCompromisosUiState
import com.pagoflex.app.features.home.presentation.viewmodel.ListaCompromisosViewModel
import java.time.LocalDate

@Composable
fun ListaCompromisosScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVolver: () -> Unit,
    onVerCompromiso: (String) -> Unit
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { HomeRepository(db) }
    val viewModel: ListaCompromisosViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ListaCompromisosViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. recargar los compromisos al volver a la lista
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarCompromisos(usuarioId)
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
                            onClick = { onVerCompromiso(compromiso.id) }
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
        CompromisoInicio(
            id = "CP-2026-0102",
            organizacion = "Club Los Halcones",
            siglaOrganizacion = "CL",
            concepto = "Cuota social octubre",
            numeroCuota = 10,
            totalCuotas = 12,
            fechaVencimiento = LocalDate.of(2026, 10, 5),
            monto = 15_000L,
            recargo = 2_000L,
            estado = EstadoCompromiso.VENCIDO
        ),
        CompromisoInicio(
            id = "CP-2026-0105",
            organizacion = "Crédito Andino",
            siglaOrganizacion = "CA",
            concepto = "Crédito de consumo",
            numeroCuota = 8,
            totalCuotas = 24,
            fechaVencimiento = LocalDate.of(2026, 10, 25),
            monto = 86_400L,
            estado = EstadoCompromiso.PENDIENTE
        ),
        CompromisoInicio(
            id = "CP-2026-0104",
            organizacion = "Crédito Andino",
            siglaOrganizacion = "CA",
            concepto = "Crédito de consumo",
            numeroCuota = 7,
            totalCuotas = 24,
            fechaVencimiento = LocalDate.of(2026, 9, 25),
            monto = 86_400L,
            estado = EstadoCompromiso.PAGADO
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
