package com.pagoflex.app.core.data.remote

import android.content.Context
import androidx.core.content.edit

// token del backend; si no hay token la app trabaja solo con la base local
class SesionApi(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "pagoflex_api",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val CLAVE_TOKEN = "token"
    }

    val enLinea: Boolean
        get() = leerToken() != null

    fun guardarToken(token: String) {
        prefs.edit { putString(CLAVE_TOKEN, token) }
    }

    fun leerToken(): String? = prefs.getString(CLAVE_TOKEN, null)

    fun borrar() {
        prefs.edit { remove(CLAVE_TOKEN) }
    }
}
