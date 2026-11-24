package com.pagoflex.app.core.network.auth

import com.pagoflex.app.core.network.auth.model.SesionAutenticada
import com.pagoflex.app.core.network.auth.model.SesionUsuario
import com.pagoflex.app.core.network.auth.model.TokensSesion
import kotlinx.coroutines.flow.StateFlow

interface SessionStore {
    val sesion: StateFlow<SesionUsuario?>

    fun leerSesion(): SesionAutenticada?

    fun guardarSesion(usuario: SesionUsuario, tokens: TokensSesion)

    fun actualizarTokensSiCoincide(
        sesionKey: String,
        refreshTokenAnterior: String,
        tokens: TokensSesion
    ): Boolean

    fun borrarSesionSiCoincide(
        sesionKey: String,
        refreshTokenAnterior: String
    ): Boolean

    fun borrarSesion()
}
