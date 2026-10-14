package com.pagoflex.app.core.ui.styles

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pagoflex.app.core.ui.theme.*

object ChipColors {
    val pendienteContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipPendienteContainer else LightChipPendienteContainer
    val pendienteContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipPendienteContent else LightChipPendienteContent

    val expiradoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipExpiradoContainer else LightChipExpiradoContainer
    val expiradoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipExpiradoContent else LightChipExpiradoContent

    val pagadoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipPagadoContainer else LightChipPagadoContainer
    val pagadoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipPagadoContent else LightChipPagadoContent

    val enReviewContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipEnReviewContainer else LightChipEnReviewContainer
    val enReviewContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipEnReviewContent else LightChipEnReviewContent

    val canceladoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipCanceladoContainer else LightChipCanceladoContainer
    val canceladoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipCanceladoContent else LightChipCanceladoContent

    val abiertoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipAbiertoContainer else LightChipAbiertoContainer
    val abiertoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipAbiertoContent else LightChipAbiertoContent

    val enProgresoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipEnProgresoContainer else LightChipEnProgresoContainer
    val enProgresoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipEnProgresoContent else LightChipEnProgresoContent

    val resueltoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipResueltoContainer else LightChipResueltoContainer
    val resueltoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipResueltoContent else LightChipResueltoContent

    val atrasadoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipAtrasadoContainer else LightChipAtrasadoContainer
    val atrasadoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipAtrasadoContent else LightChipAtrasadoContent

    val aTiempoContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipATiempoContainer else LightChipATiempoContainer
    val aTiempoContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipATiempoContent else LightChipATiempoContent

    val demoraContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipDemoraContainer else LightChipDemoraContainer
    val demoraContent: Color
        @Composable get() = if (isSystemInDarkTheme()) DarkChipDemoraContent else LightChipDemoraContent
}