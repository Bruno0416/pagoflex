package com.pagoflex.app.core.data.remote

import android.content.Context
import androidx.core.content.edit

// tokens de la sesión en el backend
class SesionApi(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "pagoflex_api",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val CLAVE_ACCESO = "token"
        private const val CLAVE_RENOVACION = "token_renovacion"
    }

    fun guardar(tokens: TokensDto) {
        prefs.edit {
            putString(CLAVE_ACCESO, tokens.tokenAcceso)
            putString(CLAVE_RENOVACION, tokens.tokenRenovacion)
        }
    }

    fun leerToken(): String? = prefs.getString(CLAVE_ACCESO, null)

    fun leerRenovacion(): String? = prefs.getString(CLAVE_RENOVACION, null)

    fun borrar() {
        prefs.edit {
            remove(CLAVE_ACCESO)
            remove(CLAVE_RENOVACION)
        }
    }
}
