package com.pagoflex.app.core.ui.components.inputs

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.TextFieldShape
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = if (isPassword) {
        KeyboardOptions(keyboardType = KeyboardType.Password)
    } else {
        KeyboardOptions.Default
    },
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    @DrawableRes passwordVisibleIconRes: Int = R.drawable.ic_visibility,
    @DrawableRes passwordHiddenIconRes: Int = R.drawable.ic_visibility_off,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // borde dinámico según estado (focus / error / normal)
    val borderColor = when {
        isError -> Theme.colores.error
        isFocused -> Theme.colores.primary
        else -> Color.Transparent
    }
    val borderWidth = if (isError) 1.5.dp else if (isFocused) 1.dp else 0.dp

    // --- colores ---
    val containerBg = if (enabled) {
        Theme.colores.surfaceContainerHighest
    } else {
        Theme.colores.surfaceContainerHighest.copy(alpha = 0.38f)
    }

    val iconTint = when {
        !enabled -> Theme.colores.onSurface.copy(alpha = 0.38f)
        isError -> Theme.colores.error
        else -> Theme.colores.onSurfaceVariant
    }

    val labelColor = when {
        !enabled -> Theme.colores.onSurface.copy(alpha = 0.38f)
        isError -> Theme.colores.error
        isFocused -> Theme.colores.primary
        else -> Theme.colores.onSurfaceVariant
    }

    // efecto del contenido (normal u oculto para no mostrar la contraseña)
    val effectiveVisualTransformation = when {
        isPassword && !passwordVisible -> PasswordVisualTransformation()
        isPassword && passwordVisible -> VisualTransformation.None
        else -> visualTransformation
    }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = Theme.type.labelMedium,
                modifier = Modifier.padding(bottom = Theme.dimens.spacingS)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(Theme.dimens.componentsHeight)
                .background(color = containerBg, shape = TextFieldShape)
                .border(width = borderWidth, color = borderColor, shape = TextFieldShape),
            enabled = enabled,
            singleLine = singleLine,
            interactionSource = interactionSource,
            textStyle = Theme.type.bodyLarge.copy(
                color = if (enabled) Theme.colores.onSurface else Theme.colores.onSurface.copy(alpha = 0.38f)
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = effectiveVisualTransformation,
            cursorBrush = SolidColor(if (isError) Theme.colores.error else Theme.colores.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Theme.dimens.spacingS),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // icono text field (izquierda)
                    if (leadingIcon != null) {
                        CompositionLocalProvider(LocalContentColor provides iconTint) {
                            leadingIcon()
                        }
                        Spacer(Modifier.width(Theme.dimens.spacingS))
                    }

                    // area texto y placeholder
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = Theme.type.bodyLarge,
                                color = Theme.colores.outline.copy(alpha = 0.70f),
                            )
                        }
                        innerTextField()
                    }

                    // trailing para password o icono custom
                    if (isPassword) {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            enabled = enabled
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible) {
                                        passwordHiddenIconRes
                                    } else {
                                        passwordVisibleIconRes
                                    }
                                ),
                                contentDescription = if (passwordVisible) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                },
                                tint = iconTint
                            )
                        }
                    } else if (trailingIcon != null) {
                        Spacer(Modifier.width(Theme.dimens.spacingS))
                        CompositionLocalProvider(LocalContentColor provides iconTint) {
                            trailingIcon()
                        }
                    }
                }
            }
        )
    }
}

// ----------------- PREVIEWS -----------------

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Default TextField"
)
@Composable
private fun TextFieldPreview() {
    var text by rememberSaveable { mutableStateOf("usuario@pagoflex.cl") }
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Primary,
            shape = RoundedCornerShape(0),
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = "Correo electrónico",
                modifier = Modifier.padding(16.dp),
                placeholder = "Ingresa tu correo",
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TextFieldPasswordPreview() {
    var password by rememberSaveable { mutableStateOf("MiClaveSegura123") }
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Primary,
            shape = RoundedCornerShape(0),
        ) {
            TextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                modifier = Modifier.padding(16.dp),
                placeholder = "Contraseña",
                isPassword = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TextFieldErrorPreview() {
    PagoFlexTheme {
        Card(
            variant = ComponentVariant.Primary,
            shape = RoundedCornerShape(0),
        ) {
            TextField(
                value = "correo_invalido",
                onValueChange = {},
                label = "Correo corporativo",
                isError = true,
                modifier = Modifier.padding(16.dp),
                placeholder = "Correo electrónico",
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = null
                    )
                }
            )
        }
    }
}