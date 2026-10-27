package com.pagoflex.app.features.reportes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.ui.components.chips.FiltroChips
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.components.navigation.BotonPerfil
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.components.CardReporte
import com.pagoflex.app.features.reportes.presentation.viewmodel.BandejaReportesViewModel

@Composable
fun BandejaReportesScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVerReporte: (String) -> Unit,
    onVerPerfil: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { ReportesRepository(db) }
    val viewModel: BandejaReportesViewModel = viewModel(
        factory = viewModelFactory {
            initializer { BandejaReportesViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // recargar al volver del detalle
    LaunchedEffect(usuarioId) {
        viewModel.cargar(usuarioId)
    }

    // nulo representa "todos"
    val filtros = EstadoReporte.entries + listOf(null)
    val etiquetas = EstadoReporte.entries.associateWith { stringResource(it.labelRes) }
    val textoTodos = stringResource(R.string.rep_filtro_todos)
    val formatoFiltro = stringResource(R.string.rep_filtro_estado)

    // ---------- contenido ----------
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.rep_titulo_bandeja),
                subtitulo = stringResource(R.string.rep_subtitulo_agente, uiState.nombreAgente),
                acciones = {
                    // el avatar abre el perfil, ahí está cerrar sesión
                    BotonPerfil(uiState.inicialesAgente, uiState.avatarAgente, onVerPerfil)
                }
            )
        }
    ) { padding ->
        if (uiState.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = Theme.dimens.spacingXL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
        ) {
            // aviso de atrasados para que no se pierdan (PB-13)
            if (uiState.atrasados > 0) {
                item {
                    Card(
                        variant = ComponentVariant.Destructive,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Theme.dimens.paddingSides)
                    ) {
                        Text(
                            text = pluralStringResource(R.plurals.rep_aviso_atrasados, uiState.atrasados, uiState.atrasados),
                            style = Theme.type.bodyMedium,
                            modifier = Modifier.padding(Theme.dimens.spacingM)
                        )
                    }
                }
            }

            item {
                FiltroChips(
                    opciones = filtros,
                    seleccionada = uiState.filtro,
                    onSeleccion = viewModel::filtrar,
                    texto = { estado ->
                        if (estado == null) {
                            textoTodos
                        } else {
                            String.format(formatoFiltro, etiquetas.getValue(estado), uiState.cantidadPorEstado[estado] ?: 0)
                        }
                    }
                )
            }

            if (uiState.reportes.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.rep_sin_reportes),
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Theme.dimens.spacingL)
                    )
                }
            }

            items(uiState.reportes, key = { it.numero }) { reporte ->
                CardReporte(
                    reporte = reporte,
                    onClick = { onVerReporte(reporte.numero) },
                    modifier = Modifier.padding(Theme.dimens.paddingSides)
                )
            }
        }
    }
}
