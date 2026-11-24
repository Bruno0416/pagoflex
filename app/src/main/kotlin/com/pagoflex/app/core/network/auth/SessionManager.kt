package com.pagoflex.app.core.network.auth

import com.pagoflex.app.core.network.auth.model.SesionUsuario
import com.pagoflex.app.core.network.auth.model.TokensSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class SessionManager(private val store: SessionStore) {

    val sesion: StateFlow<SesionUsuario?> = store.sesion

    suspend fun iniciarSesion(usuario: SesionUsuario, tokens: TokensSesion) {
        withContext(Dispatchers.IO) {
            store.guardarSesion(usuario, tokens)
        }
    }

    suspend fun cerrarSesion() {
        withContext(Dispatchers.IO) {
            store.borrarSesion()
        }
    }
}
