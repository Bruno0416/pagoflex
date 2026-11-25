package com.pagoflex.app.features.reportes.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

// foto del comprobante: se achica a 1600 px y se manda en jpeg, igual que el avatar de perfil
object ComprobanteImagen {
    private const val LADO_MAXIMO = 1600

    fun comprimir(context: Context, uri: Uri): ByteArray? = runCatching {
        // 1. leer solo el tamaño para elegir cuánto reducir
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
        if (opciones.outWidth <= 0 || opciones.outHeight <= 0) return@runCatching null
        var muestra = 1
        while (maxOf(opciones.outWidth, opciones.outHeight) / muestra > LADO_MAXIMO) muestra *= 2

        // 2. decodificar reducida y pasar a jpeg
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
        } ?: return@runCatching null
        ByteArrayOutputStream().use { salida ->
            check(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, salida))
            salida.toByteArray()
        }
    }.getOrNull()
}
