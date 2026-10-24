package com.pagoflex.app.features.auth.data

import androidx.room3.withWriteTransaction
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.core.profiles.model.SesionUsuario
import com.pagoflex.app.core.security.Hasher
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AuthRepository(
    private val db: LocalDatabase
) {
    // --------------- login ---------------
    suspend fun iniciarSesion(correo: String, contrasena: String): SesionUsuario? {
        // 1. buscar acceso por correo
        val acceso = db.usuarioDao().obtenerAccesoPorCorreo(correo.trim().lowercase())
            ?: return null
        val hash = acceso.passwordHash ?: return null

        // 2. verificar contrasena
        val esValida = withContext(Dispatchers.Default) {
            Hasher.verificar(contrasena, hash)
        }

        // 3. retornar null si no es valido
        if (!esValida) return null

        // 4. retornar sesion del usuario (id + rol)
        return db.usuarioDao().obtenerSesionUsuario(acceso.idUsuario)
    }

    suspend fun obtenerSesion(usuarioId: Int): SesionUsuario? {
        return db.usuarioDao().obtenerSesionUsuario(usuarioId)
    }

    // --------------- registro ---------------
    suspend fun registrarUsuario(
        rut: String,
        nombres: String,
        apellidos: String,
        correo: String,
        contrasena: String,
        idPreguntaRecuperacion: Int,
        respuestaRecuperacion: String
    ) {
        // 1. hashear contrasena y respuesta
        val contrasenaHash = withContext(Dispatchers.Default) {
            Hasher.genHash(contrasena)
        }
        val respuestaHash = withContext(Dispatchers.Default) {
            Hasher.genHash(AuthValidators.normalizarRespuesta(respuestaRecuperacion))
        }

        // 2. insertar usuario en una operación transaccional
        db.withWriteTransaction {
            // obtener id rol
            val idRol = db.referenciasDao()
                .obtenerIdRolPorCodigo(PerfilUsuario.USUARIO_FINAL.name)
                ?: throw Exception("No se encontró el rol USUARIO_FINAL")

            // insertar usuario con su acceso
            db.usuarioDao().insertarUsuario(
                Usuario(
                    idRol = idRol,
                    rut = rut,
                    nombres = nombres,
                    apellidos = apellidos,
                    correo = correo,
                    activo = true,
                    fechaCreacion = LocalDateTime.now(),
                    passwordHash = contrasenaHash,
                    idPreguntaRecuperacion = idPreguntaRecuperacion,
                    respuestaRecuperacionHash = respuestaHash
                )
            )
        }
    }

    // --------------- recuperación ---------------
    suspend fun obtenerPreguntas() = db.preguntaRecuperacionDao().obtenerPreguntas()

    suspend fun validarIdentidadRecuperacion(
        correo: String,
        idPregunta: Int,
        respuesta: String
    ): Boolean {
        val acceso = db.usuarioDao().obtenerAccesoPorCorreo(correo.trim().lowercase())
            ?: return false
        if (acceso.idPreguntaRecuperacion != idPregunta) return false
        val hashRespuesta = acceso.respuestaRecuperacionHash ?: return false

        return withContext(Dispatchers.Default) {
            Hasher.verificar(AuthValidators.normalizarRespuesta(respuesta), hashRespuesta)
        }
    }

    suspend fun restablecerContrasena(
        correo: String,
        idPregunta: Int,
        respuesta: String,
        nuevaContrasena: String
    ): Boolean {
        // 1. verificar de nuevo la pregunta y respuesta
        if (!validarIdentidadRecuperacion(correo, idPregunta, respuesta)) return false

        // 2. actualizar la contrasena solo si la identidad coincide
        val acceso = db.usuarioDao().obtenerAccesoPorCorreo(correo.trim().lowercase())
            ?: return false
        val nuevoHash = withContext(Dispatchers.Default) {
            Hasher.genHash(nuevaContrasena)
        }
        return db.usuarioDao().actualizarContrasena(acceso.idUsuario, nuevoHash) > 0
    }
}
