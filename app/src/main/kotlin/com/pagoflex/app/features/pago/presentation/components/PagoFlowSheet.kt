package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.features.pago.data.MetodoPagoPreferences
import com.pagoflex.app.features.pago.data.PagoRepository
import com.pagoflex.app.features.pago.presentation.viewmodel.PagoViewModel
import com.pagoflex.app.features.reconocimientos.data.ReconocimientosRepository

@Composable
fun PagoFlowSheet(
    db: LocalDatabase,
    usuarioId: Int,
    folio: String,
    onCerrar: () -> Unit,
    onPagoConfirmado: (String) -> Unit
) {
    val contexto = LocalContext.current
    val repositorio = remember(db) { PagoRepository(db) }
    val preferencias = remember(contexto) { MetodoPagoPreferences(contexto) }
    val reconocimientosRepository = remember(db) { ReconocimientosRepository(db) }
    val viewModel: PagoViewModel = viewModel(
        factory = viewModelFactory {
            initializer { PagoViewModel(repositorio, preferencias, reconocimientosRepository) }
        }
    )
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. consultar el resumen actualizado al abrir la hoja
    LaunchedEffect(folio, usuarioId) {
        viewModel.iniciarPago(folio, usuarioId)
    }

    // 2. abrir el comprobante solo después de confirmar el pago
    LaunchedEffect(estado.comprobanteConfirmado) {
        val numero = estado.comprobanteConfirmado ?: return@LaunchedEffect
        viewModel.consumirComprobante()
        onPagoConfirmado(numero)
    }

    // 3. mantener el formulario y la confirmación dentro de la misma hoja
    ConfirmacionPagoSheet(
        estado = estado,
        onCerrar = {
            if (!estado.procesando) {
                viewModel.limpiarDatosTarjeta()
                onCerrar()
            }
        },
        onSeleccionarMetodo = { viewModel.seleccionarMetodo(it, usuarioId) },
        onAvanzarADatos = viewModel::avanzarADatos,
        onUsarGuardado = viewModel::usarMetodoGuardado,
        onCambiarNumeroTarjeta = viewModel::cambiarNumeroTarjeta,
        onCambiarVencimientoTarjeta = viewModel::cambiarVencimientoTarjeta,
        onCambiarCodigoSeguridad = viewModel::cambiarCodigoSeguridad,
        onValidarNumeroTarjeta = viewModel::validarNumeroTarjetaField,
        onValidarVencimientoTarjeta = viewModel::validarVencimientoTarjetaField,
        onValidarCodigoSeguridad = viewModel::validarCodigoSeguridadField,
        onRecordar = viewModel::cambiarRecordar,
        onAvanzarAConfirmacion = viewModel::avanzarAConfirmacion,
        onVolver = viewModel::volver,
        onConfirmar = { viewModel.confirmarPago(usuarioId) }
    )
}
