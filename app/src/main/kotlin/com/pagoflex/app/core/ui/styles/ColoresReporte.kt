package com.pagoflex.app.core.ui.styles

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import com.pagoflex.app.core.rules.EstadoReporte

// ---------- colores según estado del reporte ----------

val EstadoReporte.coloresCard: CardColors
    @Composable
    get() {
        val (contenedor, contenido) = when (this) {
            EstadoReporte.ABIERTO -> ChipColors.abiertoContainer to ChipColors.abiertoContent
            EstadoReporte.EN_GESTION -> ChipColors.enProgresoContainer to ChipColors.enProgresoContent
            EstadoReporte.RESUELTO -> ChipColors.resueltoContainer to ChipColors.resueltoContent
        }
        return CardDefaults.cardColors(containerColor = contenedor, contentColor = contenido)
    }

// reportes con más de 2 días hábiles abiertos (RN-23)
val coloresAtrasado: CardColors
    @Composable
    get() = CardDefaults.cardColors(
        containerColor = ChipColors.atrasadoContainer,
        contentColor = ChipColors.atrasadoContent,
    )
