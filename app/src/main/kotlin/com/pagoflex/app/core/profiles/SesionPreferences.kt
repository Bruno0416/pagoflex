package com.pagoflex.app.core.profiles

import android.content.Context
import androidx.core.content.edit

class SesionPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "pagoflex_sesion",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val SIN_SESION = -1
    }

    fun guardar(idUsuario: Int) {
        prefs.edit {
            putInt(CLAVE_ID_USUARIO, idUsuario)
        }
    }

    fun leer(): Int {
        return prefs.getInt(CLAVE_ID_USUARIO, SIN_SESION)
    }

    fun borrar() {
        prefs.edit {
            remove(CLAVE_ID_USUARIO)
        }
    }
}