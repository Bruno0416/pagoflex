package com.pagoflex.app.core.rules

import com.pagoflex.app.core.profiles.model.PerfilUsuario

// acciones de la tabla permisos por rol (anexo 7)
enum class Accion {
    VER_COMPROMISOS_PROPIOS,
    VER_COMPROMISOS_EMPRESA,
    PAGAR_COMPROMISO,
    REGISTRAR_COMPROMISO,
    ANULAR_COMPROMISO,
    REPORTAR_PROBLEMA,
    VER_REPORTES_PROPIOS,
    VER_TODOS_LOS_REPORTES,
    VER_REPORTES_EMPRESA,
    GESTIONAR_REPORTE,
}

// qué puede hacer cada rol (RNF-13, RN-22)
object PermisosPorRol {

    private val permisos: Map<PerfilUsuario, Set<Accion>> = mapOf(
        PerfilUsuario.USUARIO_FINAL to setOf(
            Accion.VER_COMPROMISOS_PROPIOS,
            Accion.PAGAR_COMPROMISO,
            Accion.REPORTAR_PROBLEMA,
            Accion.VER_REPORTES_PROPIOS,
        ),
        // el agente ve todos los compromisos solo a través de los reportes
        PerfilUsuario.AGENTE to setOf(
            Accion.VER_TODOS_LOS_REPORTES,
            Accion.GESTIONAR_REPORTE,
        ),
        // el ejecutivo solo trabaja con su empresa y ve los reportes en modo lectura
        PerfilUsuario.EJECUTIVO to setOf(
            Accion.VER_COMPROMISOS_EMPRESA,
            Accion.REGISTRAR_COMPROMISO,
            Accion.ANULAR_COMPROMISO,
            Accion.VER_REPORTES_EMPRESA,
        ),
    )

    fun puede(perfil: PerfilUsuario?, accion: Accion): Boolean =
        perfil != null && accion in permisos[perfil].orEmpty()
}
