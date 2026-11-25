package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.feedback.CircularProgressIndicator
import com.pagoflex.app.core.ui.components.overlays.BottomSheet
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.pago.data.MetodoPagoModel
import com.pagoflex.app.features.pago.presentation.model.PagoUiState
import com.pagoflex.app.features.pago.presentation.model.PasoPago

@Composable
fun ConfirmacionPagoSheet(
    estado: PagoUiState,
    onCerrar: () -> Unit,
    onSeleccionarMetodo: (MetodoPagoModel) -> Unit,
    onAvanzarAConfirmacion: () -> Unit,
    onVolver: () -> Unit,
    onConfirmar: () -> Unit
) {
    BottomSheet(
        onDismissRequest = { if (!estado.procesando) onCerrar() },
        gesturesEnabled = !estado.procesando
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.dimens.spacingL)
                .padding(bottom = Theme.dimens.spacingXL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            val comprobante = estado.comprobanteConfirmado
            // 1. mostrar el comprobante si se confirmo el pago
            if (comprobante != null) {
                // animar el check solo cuando el backend devuelve el comprobante
                val escala = remember { Animatable(0.6f) }
                LaunchedEffect(comprobante) {
                    escala.snapTo(0.6f)
                    escala.animateTo(1f, tween(durationMillis = 350))
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .scale(escala.value)
                        .size(80.dp)
                        .background(Theme.colores.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = Theme.colores.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.pago_exito_titulo),
                    style = Theme.type.titleLarge,
                    color = Theme.colores.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = stringResource(
                        R.string.pago_exito_comprobante,
                        comprobante
                    ),
                    style = Theme.type.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Button(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.pago_cerrar))
                }
            } else if (estado.procesando) {
                // mostrar la carga mientras se espera la respuesta del backend
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Theme.dimens.spacingL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text(
                        text = stringResource(R.string.pago_procesando),
                        style = Theme.type.titleMedium
                    )
                }
            } else {
                // 1. continuar el flujo si el pago sigue pendiente
                Text(text = stringResource(R.string.pago_titulo), style = Theme.type.titleLarge)

                if (estado.cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                } else {
                    estado.resumen?.let { ResumenCompromiso(it) }
                    estado.error?.let { error ->
                        val mensaje = if (error == R.string.pago_error_orden) {
                            stringResource(error, estado.folioBloqueante.orEmpty())
                        } else stringResource(error)
                        Text(text = mensaje, style = Theme.type.bodyMedium,
                            color = Theme.colores.error)
                    }

                    if (estado.resumen == null) {
                        OutlinedButton(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.pago_cerrar))
                        }
                    } else {
                        // 2. mostrar las acciones del paso actual
                        when (estado.paso) {
                            PasoPago.METODO -> {
                                Text(
                                    text = stringResource(R.string.pago_elegir_metodo),
                                    style = Theme.type.titleMedium
                                )
                                MetodoPagoModel.entries.forEach { metodo ->
                                    OpcionMedioPago(
                                        nombre = metodo.nombreCatalogo,
                                        seleccionado = estado.metodo == metodo,
                                        habilitado = !estado.procesando,
                                        onSeleccionar = { onSeleccionarMetodo(metodo) }
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.pago_simulacion),
                                    style = Theme.type.bodySmall,
                                    color = Theme.colores.onSurfaceVariant
                                )
                                Button(
                                    onClick = onAvanzarAConfirmacion,
                                    enabled = estado.metodo != null,
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text(stringResource(R.string.pago_continuar)) }
                            }

                            PasoPago.CONFIRMACION -> {
                                Text(
                                    text = stringResource(R.string.pago_confirmar_titulo),
                                    style = Theme.type.titleMedium
                                )
                                Text(
                                    text = stringResource(
                                        R.string.pago_medio_elegido,
                                        estado.metodo?.nombreCatalogo.orEmpty()
                                    ),
                                    style = Theme.type.bodyLarge
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
                                ) {
                                    if (!estado.resultadoIncierto) {
                                        OutlinedButton(
                                            onClick = onVolver,
                                            enabled = !estado.procesando,
                                            modifier = Modifier.weight(1f)
                                        ) { Text(stringResource(R.string.pago_volver)) }
                                    }
                                    Button(
                                        onClick = onConfirmar,
                                        enabled = !estado.procesando,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(stringResource(
                                            if (estado.resultadoIncierto) R.string.pago_reintentar
                                            else R.string.pago_confirmar
                                        ))
                                    }
                                }
                            }
                        }
                        if (!estado.procesando) {
                            OutlinedButton(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(
                                    if (estado.resultadoIncierto) R.string.pago_cerrar
                                    else R.string.pago_cancelar
                                ))
                            }
                        }
                    }
                }
            }
        }
    }
}
