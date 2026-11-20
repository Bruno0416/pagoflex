package com.pagoflex.app.core.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CancellationException

// al iniciar sesión también se abre la sesión del backend; cobranza y reportes la necesitan
class ConexionBackend(private val context: Context) {

    private val sesion = SesionApi(context)

    suspend fun conectar(correo: String, contrasena: String) {
        // 1. olvidar los tokens de la sesión anterior
        sesion.borrar()

        // 2. pedir tokens nuevos con las mismas credenciales
        try {
            val api = ApiCliente.api<AuthApi>(context)
            sesion.guardar(llamar { api.iniciarSesion(LoginBody(correo.trim(), contrasena)) })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // sin backend las pantallas de cobranza y reportes muestran el error de conexión
            Log.w("PagoFlexApi", "no se pudo iniciar sesion en el backend", e)
        }
    }

    fun cerrar() = sesion.borrar()
}
