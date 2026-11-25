package com.pagoflex.app.features.perfil

import com.pagoflex.app.features.perfil.data.remote.PerfilApi
import com.pagoflex.app.features.perfil.data.remote.dto.request.ActualizarPerfilRequest
import com.pagoflex.app.features.perfil.data.remote.dto.request.CambiarContrasenaRequest
import com.pagoflex.app.features.perfil.data.remote.dto.response.ActualizarPerfilResponse
import com.pagoflex.app.features.perfil.data.remote.dto.response.PerfilResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.io.IOException

class FakePerfilApi : PerfilApi {
    val perfil = PerfilResponse(
        id = "usuario-prueba",
        nombres = "Camila",
        apellidos = "Rojas",
        correo = "camila@duocuc.cl",
        urlAvatar = null
    )

    var sinConexion = false

    override suspend fun obtenerPerfil(): PerfilResponse {
        if (sinConexion) throw IOException("Servidor apagado")
        return perfil
    }

    // No se utilizan en las pruebas de edicion y validacion.
    override suspend fun actualizar(request: ActualizarPerfilRequest): ActualizarPerfilResponse = TODO()
    override suspend fun cambiarContrasena(request: CambiarContrasenaRequest): Unit = TODO()
    override suspend fun descargarAvatar(): ResponseBody = TODO()
    override suspend fun guardarAvatar(imagen: MultipartBody.Part): PerfilResponse = TODO()
    override suspend fun eliminarAvatar(): Unit = TODO()
}
