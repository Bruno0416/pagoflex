package com.pagoflex.app.features.pago.presentation.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object VencimientoTarjetaTransformation : VisualTransformation {
    private val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            if (offset > 2) offset + 1 else offset

        override fun transformedToOriginal(offset: Int): Int =
            if (offset > 2) offset - 1 else offset
    }

    override fun filter(text: AnnotatedString): TransformedText {
        if (text.length <= 2) return TransformedText(text, OffsetMapping.Identity)

        // 1. mostrar el separador sin insertarlo en el valor del campo
        val fecha = text.text.take(2) + "/" + text.text.drop(2)

        // 2. conservar la posición del cursor después del separador
        return TransformedText(AnnotatedString(fecha), offsetMapping)
    }
}
