package com.pagoflex.app.core.ui.styles

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pagoflex.app.core.ui.theme.DarkChipATiempoContainer
import com.pagoflex.app.core.ui.theme.DarkChipATiempoContent
import com.pagoflex.app.core.ui.theme.DarkChipAbiertoContainer
import com.pagoflex.app.core.ui.theme.DarkChipAbiertoContent
import com.pagoflex.app.core.ui.theme.DarkChipAtrasadoContainer
import com.pagoflex.app.core.ui.theme.DarkChipAtrasadoContent
import com.pagoflex.app.core.ui.theme.DarkChipCanceladoContainer
import com.pagoflex.app.core.ui.theme.DarkChipCanceladoContent
import com.pagoflex.app.core.ui.theme.DarkChipDemoraContainer
import com.pagoflex.app.core.ui.theme.DarkChipDemoraContent
import com.pagoflex.app.core.ui.theme.DarkChipEnProgresoContainer
import com.pagoflex.app.core.ui.theme.DarkChipEnProgresoContent
import com.pagoflex.app.core.ui.theme.DarkChipEnReviewContainer
import com.pagoflex.app.core.ui.theme.DarkChipEnReviewContent
import com.pagoflex.app.core.ui.theme.DarkChipExpiradoContainer
import com.pagoflex.app.core.ui.theme.DarkChipExpiradoContent
import com.pagoflex.app.core.ui.theme.DarkChipPagadoContainer
import com.pagoflex.app.core.ui.theme.DarkChipPagadoContent
import com.pagoflex.app.core.ui.theme.DarkChipPendienteContainer
import com.pagoflex.app.core.ui.theme.DarkChipPendienteContent
import com.pagoflex.app.core.ui.theme.DarkChipResueltoContainer
import com.pagoflex.app.core.ui.theme.DarkChipResueltoContent
import com.pagoflex.app.core.ui.theme.LightChipATiempoContainer
import com.pagoflex.app.core.ui.theme.LightChipATiempoContent
import com.pagoflex.app.core.ui.theme.LightChipAbiertoContainer
import com.pagoflex.app.core.ui.theme.LightChipAbiertoContent
import com.pagoflex.app.core.ui.theme.LightChipAtrasadoContainer
import com.pagoflex.app.core.ui.theme.LightChipAtrasadoContent
import com.pagoflex.app.core.ui.theme.LightChipCanceladoContainer
import com.pagoflex.app.core.ui.theme.LightChipCanceladoContent
import com.pagoflex.app.core.ui.theme.LightChipDemoraContainer
import com.pagoflex.app.core.ui.theme.LightChipDemoraContent
import com.pagoflex.app.core.ui.theme.LightChipEnProgresoContainer
import com.pagoflex.app.core.ui.theme.LightChipEnProgresoContent
import com.pagoflex.app.core.ui.theme.LightChipEnReviewContainer
import com.pagoflex.app.core.ui.theme.LightChipEnReviewContent
import com.pagoflex.app.core.ui.theme.LightChipExpiradoContainer
import com.pagoflex.app.core.ui.theme.LightChipExpiradoContent
import com.pagoflex.app.core.ui.theme.LightChipPagadoContainer
import com.pagoflex.app.core.ui.theme.LightChipPagadoContent
import com.pagoflex.app.core.ui.theme.LightChipPendienteContainer
import com.pagoflex.app.core.ui.theme.LightChipPendienteContent
import com.pagoflex.app.core.ui.theme.LightChipResueltoContainer
import com.pagoflex.app.core.ui.theme.LightChipResueltoContent

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