package com.pagoflex.app.core.data.remote

import android.content.Context
import kotlinx.coroutines.CancellationException

// nombre de la cuenta en sesión para la barra superior
data class Cuenta(val nombres: String, val apellidos: String) {
    val nombre: String get() = nombres.trim().substringBefore(" ")
    val iniciales: String get() = (nombres.trim().take(1) + apellidos.trim().take(1)).uppercase()
}

class CuentaRemota(private val api: AuthApi) {

    companion object {
        fun crear(context: Context): CuentaRemota = CuentaRemota(ApiCliente.api(context))
    }

    // null si no hay sesión o el backend no responde
    suspend fun actual(): Cuenta? = try {
        llamar { api.perfil() }.let { Cuenta(it.nombres, it.apellidos) }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}
