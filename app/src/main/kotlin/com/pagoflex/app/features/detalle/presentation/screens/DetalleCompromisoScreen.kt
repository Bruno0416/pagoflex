package com.pagoflex.app.features.detalle.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.features.detalle.data.DetalleRepository
import com.pagoflex.app.features.detalle.presentation.components.DetalleContent
import com.pagoflex.app.features.detalle.presentation.viewmodel.DetalleViewModel
import com.pagoflex.app.features.pago.presentation.components.PagoFlowSheet

@Composable
fun DetalleCompromisoScreen(
    db: LocalDatabase,
    usuarioId: Int,
    folio: String,
    onVolver: () -> Unit,
    onPagoConfirmado: (String) -> Unit,
    onReportar: (() -> Unit)? = null
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { DetalleRepository(db) }

    val viewModel: DetalleViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DetalleViewModel(repositorio) }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarPago by rememberSaveable(folio, usuarioId) { mutableStateOf(false) }

    // 1. actualizar el detalle al regresar del pago
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarDetalle(folio, usuarioId)
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

        uiState.detalle == null -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.detalle_no_encontrado))
        }

        else -> DetalleContent(
            uiState = uiState,
            onVolver = onVolver,
            onPagar = { mostrarPago = true },
            onReportar = onReportar
        )
    }

    // 3. abrir el pago desde el detalle
    if (mostrarPago) {
        PagoFlowSheet(
            db = db,
            usuarioId = usuarioId,
            folio = folio,
            onCerrar = {
                mostrarPago = false
                viewModel.cargarDetalle(folio, usuarioId)
            },
            onPagoConfirmado = { numero ->
                mostrarPago = false
                onPagoConfirmado(numero)
            }
        )
    }
}
