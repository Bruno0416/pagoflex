package com.pagoflex.app.features.cumplimiento.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.feedback.CircularProgressIndicator
import com.pagoflex.app.features.cumplimiento.data.CumplimientoRepository
import com.pagoflex.app.features.cumplimiento.presentation.components.CumplimientoContent
import com.pagoflex.app.features.cumplimiento.presentation.viewmodel.CumplimientoViewModel

@Composable
fun CumplimientoScreen(
    onVolver: () -> Unit,
    onVerReconocimientos: (() -> Unit)? = null
) {
    val repositorio = remember { CumplimientoRepository() }
    val viewModel: CumplimientoViewModel = viewModel(
        factory = viewModelFactory { initializer { CumplimientoViewModel(repositorio) } }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. actualizar el resultado cada vez que regresa a la pantalla
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarCumplimiento()
    }

    when {
        uiState.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        uiState.mensajeError != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(uiState.mensajeError ?: R.string.cumplimiento_error_cargar))
        }
        uiState.resultado != null -> CumplimientoContent(
            resultado = checkNotNull(uiState.resultado),
            onVolver = onVolver,
            onVerReconocimientos = onVerReconocimientos
        )
    }
}
