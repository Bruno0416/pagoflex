package com.pagoflex.app.features.perfil.data

import android.graphics.Bitmap
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.dao.PerfilUsuarioRow
import com.pagoflex.app.core.security.Hasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

// Exclusivamente para el usuario identificado por la sesión
class PerfilRepository(private val db: LocalDatabase) {
    enum class Resultado { GUARDADO, CORREO_OCUPADO, CONTRASENA_INCORRECTA, SIN_USUARIO }

    // ---------- consulta y actualización ----------
    suspend fun obtenerPerfil(usuarioId: Int): PerfilUsuarioRow? = withContext(Dispatchers.IO) {
        db.usuarioDao().obtenerPerfil(usuarioId)
    }

    suspend fun actualizarDatos(
        usuarioId: Int,
        correoAnterior: String,
        nombres: String,
        apellidos: String,
        correo: String,
        contrasenaActual: String
    ): Resultado {
        // 1. comprobar si se modifico el correo
        val dao = db.usuarioDao()
        val cambioCorreo = correoAnterior != correo
        if (cambioCorreo) {
            // 2. verificar la contrasena actual antes de cambiar el correo
            val hash = withContext(Dispatchers.IO) { dao.obtenerHashParaPerfil(usuarioId) }
                ?: return Resultado.SIN_USUARIO
            val coincide = withContext(Dispatchers.Default) {
                Hasher.verificar(contrasenaActual, hash)
            }
            if (!coincide) return Resultado.CONTRASENA_INCORRECTA
            val otroId = withContext(Dispatchers.IO) { dao.obtenerIdPorCorreo(correo) }
            if (otroId != null && otroId != usuarioId) return Resultado.CORREO_OCUPADO
        }
        // 3. actualizar solo los datos de perfil; la restricción unique protege el correo
        val filas = withContext(Dispatchers.IO) {
            dao.actualizarPerfil(usuarioId, nombres, apellidos, correo, correoAnterior)
        }
        return if (filas == 1) Resultado.GUARDADO else Resultado.SIN_USUARIO
    }

    // ---------- fotografía ----------
    suspend fun guardarAvatar(usuarioId: Int, imagen: Bitmap): ByteArray {
        // 1. reducir y comprimir la fotografía sin bloquear la interfaz
        val bytes = withContext(Dispatchers.Default) {
            val maximo = maxOf(imagen.width, imagen.height)
            require(maximo > 0) { "Imagen vacía" }
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
        require(bytes.size <= 256 * 1024) { "Imagen demasiado grande" }
        // 2. persistir solo los bytes del avatar
        val filas = withContext(Dispatchers.IO) {
            db.usuarioDao().actualizarAvatar(usuarioId, bytes)
        }
        check(filas == 1) { "Usuario no disponible" }
        return bytes
    }

    suspend fun eliminarAvatar(usuarioId: Int) = withContext(Dispatchers.IO) {
        check(db.usuarioDao().actualizarAvatar(usuarioId, null) == 1) {
            "Usuario no disponible"
        }
    }
}
