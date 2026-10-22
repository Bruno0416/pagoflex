package com.pagoflex.app.features.cobranza.presentation.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.components.chips.FiltroChips
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.presentation.components.CardCompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.components.SelectorMes
import com.pagoflex.app.features.cobranza.presentation.viewmodel.CompromisosEmpresaViewModel
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso

@Composable
fun CompromisosEmpresaScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVerCompromiso: (String) -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { CobranzaRepository(db) }
    val viewModel: CompromisosEmpresaViewModel = viewModel(
        factory = viewModelFactory {
            initializer { CompromisosEmpresaViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // recargar al volver del detalle por si se anuló algo
    LaunchedEffect(usuarioId) {
        viewModel.cargar(usuarioId)
    }

    // nulo representa "todos"
    val filtros = listOf<EstadoCompromiso?>(null) + EstadoCompromiso.entries
    val textoTodos = stringResource(R.string.cob_filtro_todos)
    val etiquetas = EstadoCompromiso.entries.associateWith { stringResource(it.labelRes) }

    // ---------- contenido ----------
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.cob_titulo_compromisos),
                subtitulo = uiState.ejecutivo?.empresa
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
            item {
                SelectorMes(
                    mes = uiState.mes,
                    onAnterior = viewModel::mesAnterior,
                    onSiguiente = viewModel::mesSiguiente,
                    modifier = Modifier.padding(Theme.dimens.paddingSides)
                )
            }

            item {
                FiltroChips(
                    opciones = filtros,
                    seleccionada = uiState.filtro,
                    onSeleccion = viewModel::filtrar,
                    texto = { estado -> estado?.let { etiquetas.getValue(it) } ?: textoTodos }
                )
            }

            if (uiState.compromisos.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.cob_sin_resultados),
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Theme.dimens.spacingL)
                    )
                }
            }

            items(uiState.compromisos, key = { it.folio }) { compromiso ->
                CardCompromisoEmpresa(
                    compromiso = compromiso,
                    onClick = { onVerCompromiso(compromiso.folio) },
                    modifier = Modifier.padding(Theme.dimens.paddingSides)
                )
            }
        }
    }
}
