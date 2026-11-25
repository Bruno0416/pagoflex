package com.pagoflex.app.core.network.auth

import com.pagoflex.app.core.network.auth.model.PerfilUsuario
import com.pagoflex.app.core.rules.Accion
import com.pagoflex.app.core.rules.PermisosPorRol

class ControlAcceso(private val store: SessionStore) {

    fun perfilActual(): PerfilUsuario? = store.sesion.value?.perfil

    fun puede(accion: Accion): Boolean =
        PermisosPorRol.puede(perfilActual(), accion)
}
