package com.pagoflex.app.core.data.remote

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.perfil.data.remote.PerfilApi
import kotlinx.coroutines.CancellationException

// nombre de la cuenta en sesión para la barra superior
data class Cuenta(val nombres: String, val apellidos: String) {
    val nombre: String get() = nombres.trim().substringBefore(" ")
    val iniciales: String get() = (nombres.trim().take(1) + apellidos.trim().take(1)).uppercase()
}

class CuentaRemota(private val api: PerfilApi) {

    companion object {
        fun crear(): CuentaRemota = CuentaRemota(RetrofitClient.retrofit.create(PerfilApi::class.java))
    }

    // null si no hay sesión o el backend no responde
    suspend fun actual(): Cuenta? = try {
        api.obtenerPerfil().let { Cuenta(it.nombres, it.apellidos) }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}
