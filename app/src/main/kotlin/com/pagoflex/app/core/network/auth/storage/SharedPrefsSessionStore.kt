package com.pagoflex.app.core.network.auth.storage

import android.content.Context
import android.content.SharedPreferences
import com.pagoflex.app.core.network.auth.SessionStore
import com.pagoflex.app.core.network.auth.model.PerfilUsuario
import com.pagoflex.app.core.network.auth.model.SesionAutenticada
import com.pagoflex.app.core.network.auth.model.SesionUsuario
import com.pagoflex.app.core.network.auth.model.TokensSesion
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPrefsSessionStore(context: Context) : SessionStore {

    private val preferences: SharedPreferences = context.applicationContext
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    private val _sesion = MutableStateFlow(leerSesionPersistida()?.usuario)

    override val sesion: StateFlow<SesionUsuario?> = _sesion.asStateFlow()

    @Synchronized
    override fun leerSesion(): SesionAutenticada? = leerSesionPersistida()

    @Synchronized
    override fun guardarSesion(usuario: SesionUsuario, tokens: TokensSesion) {
        require(tokens.accessToken.isNotBlank())
        require(tokens.refreshToken.isNotBlank())

        guardarCambios {
            remove(CLAVE_ID_ANTIGUO)
            putString(CLAVE_PERFIL, usuario.perfil.name)
            putString(CLAVE_ACCESS, tokens.accessToken)
            putString(CLAVE_REFRESH, tokens.refreshToken)
            putString(CLAVE_SESION, UUID.randomUUID().toString())
        }
        _sesion.value = usuario
    }

    @Synchronized
    override fun actualizarTokensSiCoincide(
        sesionKey: String,
        refreshTokenAnterior: String,
        tokens: TokensSesion
    ): Boolean {
        require(tokens.accessToken.isNotBlank())
        require(tokens.refreshToken.isNotBlank())

        val actual = leerSesionPersistida() ?: return false
        if (actual.sesionKey != sesionKey ||
            actual.tokens.refreshToken != refreshTokenAnterior
        ) {
            return false
        }

        guardarCambios {
            putString(CLAVE_ACCESS, tokens.accessToken)
            putString(CLAVE_REFRESH, tokens.refreshToken)
        }
        return true
    }

    @Synchronized
    override fun borrarSesionSiCoincide(
        sesionKey: String,
        refreshTokenAnterior: String
    ): Boolean {
        val actual = leerSesionPersistida() ?: return false
        if (actual.sesionKey != sesionKey ||
            actual.tokens.refreshToken != refreshTokenAnterior
        ) {
            return false
        }
        borrarSesion()
        return true
    }

    @Synchronized
    override fun borrarSesion() {
        guardarCambios { clear() }
        _sesion.value = null
    }

    private fun guardarCambios(block: SharedPreferences.Editor.() -> Unit) {
        val guardado = preferences.edit().apply(block).commit()
        check(guardado) { "No se pudo persistir el estado de la sesión" }
    }

    private fun leerSesionPersistida(): SesionAutenticada? {
        val perfil = preferences.getString(CLAVE_PERFIL, null)
            ?.let { valor ->
                PerfilUsuario.entries.firstOrNull { it.name == valor }
            }
            ?: return null
        val accessToken = preferences.getString(CLAVE_ACCESS, null)
            ?.takeIf { it.isNotBlank() } ?: return null
        val refreshToken = preferences.getString(CLAVE_REFRESH, null)
            ?.takeIf { it.isNotBlank() } ?: return null
        val sesionKey = preferences.getString(CLAVE_SESION, null)
            ?.takeIf { it.isNotBlank() } ?: return null

        return SesionAutenticada(
            usuario = SesionUsuario(perfil),
            tokens = TokensSesion(accessToken, refreshToken),
            sesionKey = sesionKey
        )
    }

    private companion object {
        const val ARCHIVO = "pagoflex_auth_session"
        const val CLAVE_ID_ANTIGUO = "usuario_id"
        const val CLAVE_PERFIL = "perfil"
        const val CLAVE_ACCESS = "access_token"
        const val CLAVE_REFRESH = "refresh_token"
        const val CLAVE_SESION = "sesion_key"
    }
}
