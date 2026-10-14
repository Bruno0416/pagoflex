package com.pagoflex.app.features.home.ui.screens

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.home.model.CompromisoInicio
import com.pagoflex.app.features.home.model.EstadoCompromiso
import com.pagoflex.app.features.home.model.HomeUiState
import com.pagoflex.app.features.home.ui.components.AvisoVencimiento
import com.pagoflex.app.features.home.ui.components.CardCompromiso
import com.pagoflex.app.features.home.ui.components.CardResumen
import com.pagoflex.app.features.home.ui.components.HomeAppBar
import com.pagoflex.app.features.home.ui.components.SeccionCompromisos
import com.pagoflex.app.features.home.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onVerTodos: () -> Unit = {
        println("Home: Ver todos")
    },
    onVerCompromiso: (String) -> Unit = { id ->
        println("Home: Compromiso seleccionado: $id")
    }
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {

        // ----- mock temporal (hasta que tengamos bd) -----
        val compromisosMock = listOf(

            CompromisoInicio(
                id = "comp-001",
                organizacion = "Club Los Halcones",
                siglaOrganizacion = "CH",
                concepto = "Cuota social oct.",
                numeroCuota = 10,
                totalCuotas = 12,
                fechaVencimiento = LocalDate.of(2026, 10, 5),
                monto = 17_000L,
                estado = EstadoCompromiso.PENDIENTE
            ),

            CompromisoInicio(
                id = "comp-002",
                organizacion = "Plaza Oriente",
                siglaOrganizacion = "PO",
                concepto = "Arriendo oct.",
                numeroCuota = 10,
                totalCuotas = 12,
                fechaVencimiento = LocalDate.of(2026, 10, 10),
                monto = 520_000L,
                estado = EstadoCompromiso.PENDIENTE
            ),

            CompromisoInicio(
                id = "comp-003",
                organizacion = "Manos Abiertas",
                siglaOrganizacion = "MA",
                concepto = "Aporte octubre",
                fechaVencimiento = LocalDate.of(2026, 10, 15),
                monto = 10_000L,
                estado = EstadoCompromiso.PENDIENTE
            ),

            CompromisoInicio(
                id = "comp-004",
                organizacion = "Crédito Andino",
                siglaOrganizacion = "CA",
                concepto = "Crédito",
                numeroCuota = 8,
                totalCuotas = 24,
                fechaVencimiento = LocalDate.of(2026, 10, 25),
                monto = 86_400L,
                estado = EstadoCompromiso.PENDIENTE
            )
        )

        // ----- entregar datos al viewmodel -----

        viewModel.cargarInicio(
            nombreUsuario = "Camila",
            ultimaActualizacion = LocalDateTime.of(
                2026, 10, 6, 8, 12
            ),
            fechaReferencia = LocalDate.of(2026, 10, 6),
            porcentajeCumplimiento = 91.7,
            compromisos = compromisosMock
        )
    }

// ----- mostrar contenido -----

    HomeContent(
        uiState = uiState,
        onVerTodos = onVerTodos,
        onVerCompromiso = onVerCompromiso
    )

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
        // AppNavegacion ya gestiona los insets del sistema.
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
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .consumeWindowInsets(paddingInterno)
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



@Preview(
    showSystemUi = true,
    showBackground = true
)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun HomeScreenPreview() {

    val viewModel = remember { HomeViewModel() }

    PagoFlexTheme {
        // Este Preview no se renderiza dentro del Scaffold de AppNavegacion.
        Box(Modifier.statusBarsPadding()) {
            HomeScreen(viewModel = viewModel)
        }
    }
}
