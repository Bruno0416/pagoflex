package com.pagoflex.app.features.pago.data

import android.content.Context
import androidx.core.content.edit

/** Recuerda solo la terminacion de la tarjeta o el tipo de transferencia. */
class MetodoPagoPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "pagoflex_metodos_simulados", Context.MODE_PRIVATE
    )

    // ---------- consulta de preferencias ----------
    fun obtenerIdentificador(usuarioId: Int, metodo: MetodoPago): String? {
        if (usuarioId <= 0) return null
        val guardado = prefs.getString(clave(usuarioId, metodo), null) ?: return null
        return if (metodo == MetodoPago.TRANSFERENCIA) {
            metodo.nombreCatalogo
        } else {
            "•••• $guardado"
        }
    }

    // ---------- persistencia de preferencias ----------
    fun recordar(usuarioId: Int, metodo: MetodoPago, numeroTarjeta: String) {
        // 1. obtener un identificador que no exponga datos completos de tarjeta
        if (usuarioId <= 0) return
        val identificador = if (metodo == MetodoPago.TRANSFERENCIA) {
            metodo.nombreCatalogo
        } else {
            numeroTarjeta.filter(Char::isDigit).takeLast(4)
        }
        if (identificador.isBlank()) return

        // 2. guardar solo la terminacion o el tipo seleccionado
        prefs.edit { putString(clave(usuarioId, metodo), identificador) }
    }

    // --- clave de preferencia ---
    private fun clave(usuarioId: Int, metodo: MetodoPago) =
        "usuario_${usuarioId}_${metodo.name}_identificador_v2"
}
