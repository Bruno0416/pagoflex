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
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.historial.data.HistorialRepository
import com.pagoflex.app.features.historial.presentation.components.CardPagoHistorial
import com.pagoflex.app.features.historial.presentation.components.FiltrosHistorial
import com.pagoflex.app.features.historial.presentation.model.HistorialUiState
import com.pagoflex.app.features.historial.presentation.viewmodel.HistorialViewModel
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistorialScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVerComprobante: (String) -> Unit
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { HistorialRepository(db) }
    val viewModel: HistorialViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HistorialViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. recargar el historial al volver de un comprobante
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarHistorial(usuarioId)
    }

    HistorialContent(uiState, onVerComprobante)
}

@Composable
private fun HistorialContent(
    uiState: HistorialUiState,
    onVerComprobante: (String) -> Unit
) {
    val etiquetaEmpresa = stringResource(R.string.historial_filtro_empresa)
    val todasEmpresas = stringResource(R.string.historial_filtro_todas)
    val todosAnios = stringResource(R.string.historial_filtro_todos)

    var empresaSeleccionada by remember(etiquetaEmpresa) { mutableStateOf(etiquetaEmpresa) }
    var anioSeleccionado by remember(uiState.comprobantes, todosAnios) {
        mutableStateOf(
            uiState.comprobantes.firstOrNull()?.fechaHora?.year?.toString() ?: todosAnios
        )
    }

    val empresas = listOf(todasEmpresas) + uiState.comprobantes
        .map { it.empresa }.distinct().sorted()
    val anios = listOf(todosAnios) + uiState.comprobantes
        .map { it.fechaHora.year.toString() }.distinct().sortedDescending()

    val filtrados = uiState.comprobantes.filter {
        (empresaSeleccionada == etiquetaEmpresa || empresaSeleccionada == todasEmpresas ||
                it.empresa == empresaSeleccionada) &&
                (anioSeleccionado == todosAnios || it.fechaHora.year.toString() == anioSeleccionado)
    }
    val agrupados = filtrados.groupBy { YearMonth.from(it.fechaHora) }

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
        if (uiState.cargando) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.mensajeError != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                Text(stringResource(uiState.mensajeError))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(Theme.dimens.paddingSides),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
            ) {
                FiltrosHistorial(
                    empresaSeleccionada, empresas, { empresaSeleccionada = it },
                    anioSeleccionado, anios, { anioSeleccionado = it }
                )

                if (filtrados.isEmpty()) {
                    Text(
                        if (uiState.comprobantes.isEmpty()) stringResource(R.string.historial_vacio)
                        else stringResource(R.string.historial_sin_resultados),
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.onSurfaceVariant
                    )
                }

                agrupados.forEach { (mes, comprobantes) ->
                    Text(
                        mes.format(
                            DateTimeFormatter.ofPattern(
                                "MMMM yyyy",
                                Locale.forLanguageTag("es-CL")
                            )
                        )
                            .replaceFirstChar { it.uppercaseChar() },
                        style = Theme.type.titleMedium,
                        color = Theme.colores.onSurfaceVariant
                    )
                    comprobantes.forEach { comprobante ->
                        CardPagoHistorial(
                            comprobante = comprobante,
                            onClick = { onVerComprobante(comprobante.numeroComprobante) }
                        )
                    }
                }
            }
        }
    }
}

// ---------- previews ----------
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistorialPreview() {
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
        fechaVencimiento = java.time.LocalDate.of(2026, 9, 25),
        montoCuota = 86_400,
        recargo = 0,
        montoPagado = 86_400,
        medioPago = "Tarjeta de débito",
        canal = "Portal de pagos"
    )
    PagoFlexTheme {
        HistorialContent(
            uiState = HistorialUiState(comprobantes = listOf(ejemplo), cargando = false),
            onVerComprobante = {}
        )
    }
}
