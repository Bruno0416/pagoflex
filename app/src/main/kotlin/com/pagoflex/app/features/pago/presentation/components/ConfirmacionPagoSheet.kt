package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                Text(
                    text = stringResource(R.string.pago_exito_titulo),
                    style = Theme.type.titleLarge,
                    color = Theme.colores.primary
                )
                Text(
                    text = stringResource(
                        R.string.pago_exito_comprobante,
                        comprobante
                    ),
                    style = Theme.type.bodyLarge
                )
                Button(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.pago_cerrar))
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
                                        if (estado.procesando) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                        } else {
                                            Text(stringResource(
                                                if (estado.resultadoIncierto) R.string.pago_reintentar
                                                else R.string.pago_confirmar
                                            ))
                                        }
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
