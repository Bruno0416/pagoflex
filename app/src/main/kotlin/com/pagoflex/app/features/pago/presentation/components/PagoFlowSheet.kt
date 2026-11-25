package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.features.pago.data.PagoRepository
import com.pagoflex.app.features.pago.presentation.viewmodel.PagoViewModel
import java.util.UUID

@Composable
fun PagoFlowSheet(folio: String, onCerrar: () -> Unit) {
    // 1. conservar la identidad de la hoja durante la sesion actual
    val sesionId = rememberSaveable { UUID.randomUUID().toString() }
    val repositorio = remember { PagoRepository() }
    val viewModel: PagoViewModel = viewModel(
        factory = viewModelFactory { initializer { PagoViewModel(repositorio) } }
    )
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // 2. preparar el pago al abrir la hoja
    LaunchedEffect(folio, sesionId) { viewModel.iniciarPago(folio, sesionId) }

    ConfirmacionPagoSheet(
        estado = estado,
        onCerrar = onCerrar,
        onSeleccionarMetodo = viewModel::seleccionarMetodo,
        onAvanzarAConfirmacion = viewModel::avanzarAConfirmacion,
        onVolver = viewModel::volver,
        onConfirmar = viewModel::confirmarPago
    )
}
