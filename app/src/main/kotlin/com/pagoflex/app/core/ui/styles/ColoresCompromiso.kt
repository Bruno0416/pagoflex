package com.pagoflex.app.core.ui.styles

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso

// ----- colores según estado del compromiso -----

val EstadoCompromiso.coloresCard: CardColors
    @Composable
    get() {
        val (contenedor, contenido) = when (this) {

            EstadoCompromiso.PENDIENTE ->
                ChipColors.pendienteContainer to
                        ChipColors.pendienteContent

            EstadoCompromiso.VENCIDO ->
                ChipColors.expiradoContainer to
                        ChipColors.expiradoContent

            EstadoCompromiso.PAGADO ->
                ChipColors.pagadoContainer to
                        ChipColors.pagadoContent

            EstadoCompromiso.EN_REVISION ->
                ChipColors.enReviewContainer to
                        ChipColors.enReviewContent

            EstadoCompromiso.ANULADO ->
                ChipColors.canceladoContainer to
                        ChipColors.canceladoContent
        }

        return CardDefaults.cardColors(
            containerColor = contenedor,
            contentColor = contenido
        )
    }
