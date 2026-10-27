package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.overlays.BottomSheet
import com.pagoflex.app.core.ui.components.selection.RadioButton
import com.pagoflex.app.core.ui.components.selection.Switch
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.pago.data.MetodoPago
import com.pagoflex.app.features.pago.presentation.model.PagoUiState
import com.pagoflex.app.features.pago.presentation.model.PasoPago

@Composable
fun ConfirmacionPagoSheet(
    estado: PagoUiState,
    onCerrar: () -> Unit,
    onSeleccionarMetodo: (MetodoPago) -> Unit,
    onAvanzarADatos: () -> Unit,
    onUsarGuardado: (Boolean) -> Unit,
    onCambiarNumeroTarjeta: (String) -> Unit,
    onCambiarVencimientoTarjeta: (String) -> Unit,
    onCambiarCodigoSeguridad: (String) -> Unit,
    onValidarNumeroTarjeta: () -> Unit,
    onValidarVencimientoTarjeta: () -> Unit,
    onValidarCodigoSeguridad: () -> Unit,
    onRecordar: (Boolean) -> Unit,
    onAvanzarAConfirmacion: () -> Unit,
    onVolver: () -> Unit,
    onConfirmar: () -> Unit
) {
    // 1. impedir el cierre de la hoja mientras se confirma el pago
    BottomSheet(
        onDismissRequest = { if (!estado.procesando) onCerrar() },
        gesturesEnabled = !estado.procesando
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.dimens.spacingL)
                .padding(bottom = Theme.dimens.spacingXL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            Text(
                text = stringResource(R.string.pago_titulo),
                style = Theme.type.titleLarge
            )
            if (estado.cargando) {
                CircularProgressIndicator()
            } else {
                // 2. mostrar el resumen y los errores del compromiso
                val resumen = estado.resumen
                if (resumen != null) {
                    ResumenCompromiso(resumen)
                }

                if (estado.error != null) {
                    val mensaje = if (estado.error == R.string.pago_error_orden) {
                        stringResource(estado.error, estado.folioBloqueante.orEmpty())
                    } else {
                        stringResource(estado.error)
                    }
                    Text(
                        text = mensaje,
                        style = Theme.type.bodyMedium,
                        color = Theme.colores.error
                    )
                }

                if (resumen == null) {
                    OutlinedButton(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.pago_cerrar))
                    }
                } else {
                    // 3. presentar el paso seleccionado del flujo
                    when (estado.paso) {
                        PasoPago.METODO -> {
                            // 3. seleccionar el medio de pago
                            Text(
                                text = stringResource(R.string.pago_elegir_metodo),
                                style = Theme.type.titleMedium
                            )
                            MetodoPago.entries.forEach { metodo ->
                                OpcionMedioPago(
                                    nombre = metodo.nombreCatalogo,
                                    seleccionado = estado.metodo == metodo,
                                    habilitado = !estado.procesando,
                                    onSeleccionar = { onSeleccionarMetodo(metodo) }
                                )
                            }
                            Button(
                                onClick = onAvanzarADatos,
                                enabled = estado.metodo != null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.pago_continuar))
                            }
                        }

                        PasoPago.DATOS -> {
                            // 3. elegir metodo guardado o ingresar uno nuevo
                            Text(
                                text = stringResource(R.string.pago_datos_metodo),
                                style = Theme.type.titleMedium
                            )
                            if (estado.identificadorGuardado != null) {
                                RadioButton(
                                    selected = estado.usarGuardado,
                                    onClick = { onUsarGuardado(true) },
                                    enabled = !estado.procesando,
                                    label = stringResource(
                                        R.string.pago_usar_guardado, estado.identificadorGuardado
                                    )
                                )
                                RadioButton(
                                    selected = !estado.usarGuardado,
                                    onClick = { onUsarGuardado(false) },
                                    enabled = !estado.procesando,
                                    label = stringResource(R.string.pago_usar_nuevo)
                                )
                            }
                            if (!estado.usarGuardado) {
                                if (estado.metodo != MetodoPago.TRANSFERENCIA) {
                                    // 3. solicitar numero vencimiento y codigo de seguridad
                                    TextField(
                                        value = estado.numeroTarjeta,
                                        onValueChange = onCambiarNumeroTarjeta,
                                        onUnfocus = onValidarNumeroTarjeta,
                                        error = estado.errorNumeroTarjeta?.let { stringResource(it) },
                                        label = stringResource(R.string.pago_numero_tarjeta),
                                        placeholder = stringResource(R.string.pago_numero_ejemplo),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        enabled = !estado.procesando,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
                                    ) {
                                        TextField(
                                            value = estado.vencimientoTarjeta,
                                            onValueChange = onCambiarVencimientoTarjeta,
                                            visualTransformation = VencimientoTarjetaTransformation,
                                            onUnfocus = onValidarVencimientoTarjeta,
                                            error = estado.errorVencimientoTarjeta?.let { stringResource(it) },
                                            label = stringResource(R.string.pago_vencimiento_tarjeta),
                                            placeholder = stringResource(R.string.pago_vencimiento_ejemplo),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            enabled = !estado.procesando,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextField(
                                            value = estado.codigoSeguridad,
                                            onValueChange = onCambiarCodigoSeguridad,
                                            onUnfocus = onValidarCodigoSeguridad,
                                            error = estado.errorCodigoSeguridad?.let { stringResource(it) },
                                            label = stringResource(R.string.pago_codigo_seguridad),
                                            placeholder = stringResource(R.string.pago_codigo_ejemplo),
                                            isPassword = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                            enabled = !estado.procesando,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Switch(
                                    checked = estado.recordar,
                                    onCheckedChange = onRecordar,
                                    enabled = !estado.procesando,
                                    label = stringResource(R.string.pago_recordar)
                                )
                            }
                            AccionesNavegacion(
                                onVolver = onVolver,
                                onContinuar = onAvanzarAConfirmacion
                            )
                        }

                        PasoPago.CONFIRMACION -> {
                            // 3. revisar el total y confirmar el pago
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
                            if (estado.metodo != MetodoPago.TRANSFERENCIA) {
                                val terminacion = if (estado.usarGuardado) {
                                    estado.identificadorGuardado.orEmpty()
                                } else {
                                    "•••• " + estado.numeroTarjeta.filter(Char::isDigit).takeLast(4)
                                }
                                Text(
                                    text = stringResource(R.string.pago_terminacion_tarjeta, terminacion),
                                    style = Theme.type.bodyLarge
                                )
                            }
                            if (estado.procesando) {
                                CircularProgressIndicator()
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
                            ) {
                                OutlinedButton(
                                    onClick = onVolver,
                                    enabled = !estado.procesando,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.pago_volver))
                                }
                                Button(
                                    onClick = onConfirmar,
                                    enabled = !estado.procesando,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.pago_confirmar))
                                }
                            }
                        }
                    }
                    // 4. permitir cancelar antes de confirmar
                    if (!estado.procesando) {
                        OutlinedButton(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.pago_cancelar))
                        }
                    }
                }
            }
        }
    }
}

// --- acciones de navegación ---
@Composable
private fun AccionesNavegacion(
    onVolver: () -> Unit,
    onContinuar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
    ) {
        OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.pago_volver))
        }
        Button(
            onClick = onContinuar,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.pago_continuar))
        }
    }
}
