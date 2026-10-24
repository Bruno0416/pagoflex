package com.pagoflex.app.core.profiles

import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.core.rules.Accion
import com.pagoflex.app.core.rules.PermisosPorRol

// revisa el rol guardado en la base antes de cada consulta u operación
class ControlAcceso(private val db: LocalDatabase) {

    // un usuario inactivo no tiene perfil
    suspend fun perfilDe(usuarioId: Int): PerfilUsuario? =
        db.usuarioDao().obtenerSesionUsuario(usuarioId)?.perfil

    suspend fun puede(usuarioId: Int, accion: Accion): Boolean =
        PermisosPorRol.puede(perfilDe(usuarioId), accion)
}
