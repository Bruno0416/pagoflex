package com.pagoflex.app.core.data.local

import com.pagoflex.app.core.network.auth.model.PerfilUsuario
import com.pagoflex.app.core.rules.Accion
import com.pagoflex.app.core.rules.PermisosPorRol

class ControlAccesoLocal(private val db: LocalDatabase) {

    suspend fun perfilDe(usuarioId: Int): PerfilUsuario? =
        db.usuarioDao().obtenerSesionUsuario(usuarioId)?.perfil

    suspend fun puede(usuarioId: Int, accion: Accion): Boolean =
        PermisosPorRol.puede(perfilDe(usuarioId), accion)
}
