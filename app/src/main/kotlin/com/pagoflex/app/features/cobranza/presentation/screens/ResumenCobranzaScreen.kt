package com.pagoflex.app.features.cobranza.presentation.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.buttons.TextButton
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.presentation.components.CardResumenCobranza
import com.pagoflex.app.features.cobranza.presentation.components.SelectorMes
import com.pagoflex.app.features.cobranza.presentation.viewmodel.ResumenCobranzaViewModel

@Composable
fun ResumenCobranzaScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVerCompromisos: () -> Unit,
    onCerrarSesion: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { CobranzaRepository(db) }
    val viewModel: ResumenCobranzaViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ResumenCobranzaViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuarioId) {
        viewModel.cargar(usuarioId)
    }

    // ---------- contenido ----------
    Scaffold(
        // AppNavegacion ya gestiona los insets del sistema
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.cob_titulo_resumen),
                subtitulo = uiState.ejecutivo?.let {
                    stringResource(R.string.cob_subtitulo, it.empresa.orEmpty(), it.nombre)
                },
                acciones = {
                    TextButton(onClick = onCerrarSesion, size = ComponentSize.Small) {
                        Text(stringResource(R.string.ui_cerrar_sesion))
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.cargando -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.error != null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(uiState.error!!))
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(Theme.dimens.paddingSides),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
            ) {
                SelectorMes(
                    mes = uiState.mes,
                    onAnterior = { viewModel.mesAnterior(usuarioId) },
                    onSiguiente = { viewModel.mesSiguiente(usuarioId) }
                )
                CardResumenCobranza(resumen = uiState.resumen)
                OutlinedButton(onClick = onVerCompromisos) {
                    Text(stringResource(R.string.cob_ver_compromisos))
                }
            }
        }
    }
}
