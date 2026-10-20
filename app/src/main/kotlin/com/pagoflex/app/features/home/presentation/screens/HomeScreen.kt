package com.pagoflex.app.features.home.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.home.presentation.components.AvisoVencimiento
import com.pagoflex.app.features.home.presentation.components.CardCompromiso
import com.pagoflex.app.features.home.presentation.components.CardResumen
import com.pagoflex.app.features.home.presentation.components.HomeAppBar
import com.pagoflex.app.features.home.presentation.components.SeccionCompromisos
import com.pagoflex.app.features.home.presentation.model.CompromisoInicio
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.home.presentation.model.HomeUiState
import com.pagoflex.app.features.home.presentation.model.ResumenInicio
import com.pagoflex.app.features.home.presentation.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun HomeScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVerTodos: () -> Unit = {},
    onVerCompromiso: (String) -> Unit = {}
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { HomeRepository(db) }
    val viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HomeViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuarioId) {
        viewModel.cargarInicio(usuarioId)
    }

    // ---------- contenido ----------
    when {
        uiState.cargando -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        uiState.mensajeError != null -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(uiState.mensajeError!!))
        }

        else -> HomeContent(
            uiState = uiState,
            onVerTodos = onVerTodos,
            onVerCompromiso = onVerCompromiso
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onVerTodos: () -> Unit,
    onVerCompromiso: (String) -> Unit
) {
    val periodo = uiState.periodo ?: return
    val ultimaActualizacion =
        uiState.ultimaActualizacion ?: return

    Scaffold(
        // AppNavegación ya gestiona los insets del sistema.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        // ----- appbar -----
        topBar = {
            HomeAppBar(
                modifier = Modifier
                    .padding(Theme.dimens.paddingSides)
                    .padding(bottom = Theme.dimens.spacingM),
                nombreUsuario = uiState.nombreUsuario,
                ultimaActualizacion = ultimaActualizacion,
                porcentajeCompletado =
                    uiState.resumen.porcentajeCumplimiento
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                Theme.dimens.spacingM
            )
        ) {

            // 1. resumen mensual
            CardResumen(
                modifier = Modifier.padding(
                    Theme.dimens.paddingSides
                ),
                resumen = uiState.resumen,
                periodo = periodo
            )

            // 2. aviso de vencimiento
            AvisoVencimiento(
                modifier = Modifier.padding(
                    Theme.dimens.paddingSides
                ),
                resumen = uiState.resumen,
                onClick = {
                    uiState.resumen.vencidoDestacado
                        ?.id
                        ?.let(onVerCompromiso)
                }
            )

            // 3. sección tus compromisos
            SeccionCompromisos(
                onVerTodos = onVerTodos
            )

            // 4. listado de compromisos
            Column(
                modifier = Modifier.padding(
                    Theme.dimens.paddingSides
                ),
                verticalArrangement = Arrangement.spacedBy(
                    Theme.dimens.spacingS
                )
            ) {
                uiState.compromisos.forEach { compromiso ->
                    CardCompromiso(
                        compromiso = compromiso,
                        onClick = {
                            onVerCompromiso(compromiso.id)
                        }
                    )
                }
            }

            // Permitir ver la última tarjeta completa al llegar al final del scroll.
            Spacer(Modifier.height(Theme.dimens.spacingM))
        }
    }
}


// ----------------- PREVIEWS -----------------
@Preview(showSystemUi = true, showBackground = true)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun HomeScreenPreview() {
    // datos solo para visualizar el composable sin acceder a room
    val ejemplo = CompromisoInicio(
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
    )

    PagoFlexTheme {
        Box(Modifier.statusBarsPadding()) {
            HomeContent(
                uiState = HomeUiState(
                    nombreUsuario = "Camila",
                    periodo = YearMonth.of(2026, 10),
                    resumen = ResumenInicio(
                        totalPorPagar = ejemplo.total,
                        totalRecargos = ejemplo.recargo,
                        cantidadCompromisos = 1,
                        cantidadOrganizaciones = 1,
                        cantidadVencidos = 1,
                        vencidoDestacado = ejemplo
                    ),
                    compromisos = listOf(ejemplo),
                    ultimaActualizacion = LocalDateTime.of(2026, 10, 6, 8, 12)
                ),
                onVerTodos = {},
                onVerCompromiso = {}
            )
        }
    }
}
