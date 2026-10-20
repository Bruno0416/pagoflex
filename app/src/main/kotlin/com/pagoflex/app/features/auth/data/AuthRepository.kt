package com.pagoflex.app.features.auth.data

import androidx.room3.withWriteTransaction
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Credencial
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.core.profiles.model.SesionUsuario
import com.pagoflex.app.core.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AuthRepository(
    private val db: LocalDatabase
) {
    // --------------- login ---------------
    suspend fun iniciarSesion(correo: String, contrasena: String): SesionUsuario? {
        // 1. buscar credencial por correo
        val credencial =
            db.credencialDao().obtenerPorCorreo(correo.trim().lowercase()) ?: return null

        // 2. verificar contraseña
        val esValida = withContext(Dispatchers.Default) {
            PasswordHasher.verificar(
                contrasena,
                credencial.passwordHash
            )
        }

        // 3. retornar null si no es válido
        if (!esValida) return null

        // 4. retornar sesión del usuario (id + rol)
        return db.usuarioDao().obtenerSesionUsuario(credencial.idUsuario)
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
        contrasena: String
    ) {
        // 1. hashear contrasena
        val contrasenaHash = withContext(Dispatchers.Default) {
            PasswordHasher.genHash(contrasena)
        }


        // 3. insertar usuario (en una operación transaccional)
        db.withWriteTransaction {
            // obtener id rol
            val idRol = db.referenciasDao()
                .obtenerIdRolPorCodigo(PerfilUsuario.USUARIO_FINAL.name)
                ?: throw Exception("No se encontró el rol USUARIO_FINAL")

            // insertar usuario
            val idUsuario = db.usuarioDao().insertarUsuario(
                Usuario(
                    idRol = idRol,
                    rut = rut,
                    nombres = nombres,
                    apellidos = apellidos,
                    correo = correo,
                    activo = true,
                    fechaCreacion = LocalDateTime.now()
                )
            ).toInt()

            // guardar credencial
            db.credencialDao().insertarCredencial(
                Credencial(
                    idUsuario = idUsuario,
                    passwordHash = contrasenaHash
                )
            )
        }
    }
}