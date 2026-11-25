package com.pagoflex.app.features.detalle.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.feedback.CircularProgressIndicator
import com.pagoflex.app.features.detalle.data.DetalleRepository
import com.pagoflex.app.features.detalle.presentation.components.DetalleContent
import com.pagoflex.app.features.detalle.presentation.viewmodel.DetalleViewModel
import com.pagoflex.app.features.pago.presentation.components.PagoFlowSheet

@Composable
fun DetalleCompromisoScreen(
    folio: String,
    onVolver: () -> Unit,
    onReportar: (() -> Unit)? = null
) {
    val repositorio = remember { DetalleRepository() }
    val viewModel: DetalleViewModel = viewModel(
        factory = viewModelFactory { initializer { DetalleViewModel(repositorio) } }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarPago by rememberSaveable(folio) { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarDetalle(folio)
    }

    when {
        uiState.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        uiState.mensajeError != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(uiState.mensajeError!!))
        }
        uiState.detalle == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.detalle_no_encontrado))
        }
        else -> DetalleContent(
            uiState = uiState,
            onVolver = onVolver,
            onPagar = { if (uiState.puedeIniciarPago) mostrarPago = true },
            onReportar = onReportar
        )
    }

    if (mostrarPago) {
        PagoFlowSheet(
            folio = folio,
            onCerrar = {
                mostrarPago = false
                // 1. actualizar el detalle al cerrar el pago
                viewModel.cargarDetalle(folio)
            }
        )
    }
}
