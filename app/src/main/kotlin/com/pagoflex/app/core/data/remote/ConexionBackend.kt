package com.pagoflex.app.core.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CancellationException

// al iniciar sesión también se entra al backend; si no responde la app sigue solo con room
class ConexionBackend(private val context: Context) {

    private val sesion = SesionApi(context)

    suspend fun conectar(correo: String, contrasena: String) {
        // 1. olvidar el token de la sesión anterior
        sesion.borrar()

        // 2. pedir un token nuevo con las mismas credenciales
        try {
            val api = ApiCliente.retrofit(context).create(AuthApi::class.java)
            val respuesta = llamar { api.login(LoginBody(correo, contrasena)) }
            sesion.guardarToken(respuesta.token)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // sin backend o sin cuenta en él: se trabaja con la base local
            Log.w("PagoFlexApi", "sin sesion en el backend, se usa room", e)
        }
    }
}
