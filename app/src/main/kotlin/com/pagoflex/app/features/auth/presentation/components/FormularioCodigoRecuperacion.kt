package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState

@Composable
fun FormularioCodigoRecuperacion(
    uiState: RecuperarContrasenaUiState,
    setCodigo: (String) -> Unit,
    verificarCodigo: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)) {
        Text(
            stringResource(R.string.auth_api_recuperacion_codigo_descripcion, uiState.correo),
            style = Theme.type.bodyLarge.copy(color = Theme.colores.onSurfaceVariant)
        )
        Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)) {
            Text(
                stringResource(R.string.auth_api_codigo),
                style = Theme.type.labelMedium,
                color = Theme.colores.onSurfaceVariant
            )
            CodigoOtp(
                codigo = uiState.codigo,
                onCodigoChange = setCodigo,
                onDone = verificarCodigo,
                enabled = !uiState.cargando && !uiState.requiereNuevoCodigo,
                error = uiState.errorCodigo != null
            )
            uiState.errorCodigo?.let {
                Text(stringResource(it), style = Theme.type.bodySmall, color = Theme.colores.error)
            }
            if (uiState.requiereNuevoCodigo) {
                Text(
                    stringResource(R.string.auth_recuperacion_reenviar_para_continuar),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
            } else if (uiState.codigoReenviado) {
                Text(
                    stringResource(R.string.auth_api_codigo_reenviado),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.primary
                )
            }
        }
        uiState.errorRecuperacion?.let {
            Text(stringResource(it), color = Theme.colores.error)
        }
    }
}

@Composable
private fun CodigoOtp(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    onDone: () -> Unit,
    enabled: Boolean,
    error: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val etiqueta = stringResource(R.string.auth_api_codigo_ayuda)
    val forma = RoundedCornerShape(12.dp)
    val primary = Theme.colores.primary
    val activeIndex = if (codigo.length < 6) codigo.length else -1

    LaunchedEffect(Unit) {
        if (enabled) focusRequester.requestFocus()
    }

    BasicTextField(
        value = codigo,
        onValueChange = onCodigoChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .semantics { contentDescription = etiqueta },
        interactionSource = interactionSource,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboard?.hide()
            if (codigo.length == 6) onDone()
        }),
        cursorBrush = SolidColor(Theme.colores.primary),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth().height(54.dp)) {
                // El campo real ocupa toda la superficie: mantiene IME, pegado y Backspace.
                Box(modifier = Modifier.matchParentSize().alpha(0f)) {
                    innerTextField()
                }
                Row(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            enabled = enabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequester.requestFocus()
                            keyboard?.show()
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(6) { index ->
                        val active = enabled && focused && index == activeIndex
                        val borderColor = when {
                            error -> Theme.colores.error
                            active -> primary
                            else -> Theme.colores.outline.copy(alpha = 0.55f)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(Theme.colores.surfaceContainerHighest, forma)
                                .border(if (active || error) 1.5.dp else 1.dp, borderColor, forma),
                            contentAlignment = Alignment.Center
                        ) {
                            val character = codigo.getOrNull(index)
                            if (character != null) {
                                Text(
                                    text = character.toString(),
                                    style = Theme.type.headlineSmall,
                                    color = Theme.colores.onSurface
                                )
                            } else if (active) {
                                key(codigo, focused) {
                                    val transition = rememberInfiniteTransition(label = "otpCursor")
                                    val cursorAlpha by transition.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 0f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(550, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "otpCursorAlpha"
                                    )
                                    Box(
                                        Modifier
                                            .width(2.dp)
                                            .height(24.dp)
                                            .alpha(cursorAlpha)
                                            .background(primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
