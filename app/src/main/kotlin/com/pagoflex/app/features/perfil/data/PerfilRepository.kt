package com.pagoflex.app.features.perfil.data

import android.graphics.Bitmap
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.network.auth.model.SesionUsuario
import com.pagoflex.app.core.network.auth.model.TokensSesion
import com.pagoflex.app.features.perfil.data.remote.PerfilApi
import com.pagoflex.app.features.perfil.data.remote.dto.request.ActualizarPerfilRequest
import com.pagoflex.app.features.perfil.data.remote.dto.response.PerfilResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.create
import java.io.ByteArrayOutputStream

class PerfilRepository {
    private val api = RetrofitClient.retrofit.create<PerfilApi>()

    // ---------- consulta y edicion ----------
    suspend fun obtenerPerfil(): PerfilResponse = api.obtenerPerfil()

    suspend fun obtenerAvatar(): ByteArray = api.descargarAvatar().use { it.bytes() }

    suspend fun actualizarDatos(request: ActualizarPerfilRequest): PerfilResponse {
        // 1. enviar los datos personales al servicio de usuarios
        val respuesta = api.actualizar(request)
        // 2. guardar los nuevos tokens si cambio el correo
        respuesta.sesion?.let { sesion ->
            RetrofitClient.sessionManager.iniciarSesion(
                SesionUsuario(sesion.rol),
                TokensSesion(sesion.tokenAcceso, sesion.tokenRenovacion)
            )
        }
        return respuesta.perfil
    }

    // ---------- fotografia ----------
    suspend fun guardarAvatar(imagen: Bitmap): Pair<PerfilResponse, ByteArray> {
        // 1. comprimir la imagen antes de enviarla
        val bytes = withContext(Dispatchers.Default) {
            val maximo = maxOf(imagen.width, imagen.height)
            require(maximo > 0)
            val bitmap = if (maximo > 256) {
                val escala = 256f / maximo
                Bitmap.createScaledBitmap(
                    imagen,
                    (imagen.width * escala).toInt().coerceAtLeast(1),
                    (imagen.height * escala).toInt().coerceAtLeast(1),
                    true
                )
            } else imagen
            ByteArrayOutputStream().use { salida ->
                check(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, salida))
                salida.toByteArray()
            }
        }
        // 2. enviar archivo jpeg con el nombre de parte esperado
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("file", "avatar.jpg", cuerpo)
        val perfil = api.guardarAvatar(parte)
        return perfil to bytes
    }

    suspend fun eliminarAvatar() = api.eliminarAvatar()

    // --- detectar campos rechazados por el servidor ---
    fun camposInvalidos(error: HttpException): Map<String, String> {
        val texto = error.response()?.errorBody()?.string().orEmpty()
        return runCatching {
            val campos = JSONObject(texto).optJSONObject("invalid_fields") ?: return@runCatching emptyMap()
            buildMap {
                val claves = campos.keys()
                while (claves.hasNext()) {
                    val nombre = claves.next()
                    put(nombre, campos.optString(nombre))
                }
            }
        }.getOrDefault(emptyMap())
    }
}
