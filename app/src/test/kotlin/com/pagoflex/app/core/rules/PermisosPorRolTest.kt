package com.pagoflex.app.core.rules

import com.pagoflex.app.core.profiles.model.PerfilUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermisosPorRolTest {

    private fun accionesDe(perfil: PerfilUsuario): Set<Accion> =
        Accion.entries.filter { PermisosPorRol.puede(perfil, it) }.toSet()

    @Test
    fun `solo el usuario final paga y reporta problemas`() {
        PerfilUsuario.entries.forEach { perfil ->
            val esUsuario = perfil == PerfilUsuario.USUARIO_FINAL
            assertEquals(esUsuario, PermisosPorRol.puede(perfil, Accion.PAGAR_COMPROMISO))
            assertEquals(esUsuario, PermisosPorRol.puede(perfil, Accion.REPORTAR_PROBLEMA))
        }
    }

    @Test
    fun `solo el ejecutivo registra y anula compromisos (RN-20, RN-21)`() {
        PerfilUsuario.entries.forEach { perfil ->
            val esEjecutivo = perfil == PerfilUsuario.EJECUTIVO
            assertEquals(esEjecutivo, PermisosPorRol.puede(perfil, Accion.REGISTRAR_COMPROMISO))
            assertEquals(esEjecutivo, PermisosPorRol.puede(perfil, Accion.ANULAR_COMPROMISO))
        }
    }

    @Test
    fun `solo el agente gestiona reportes y no puede anular (RN-23)`() {
        assertEquals(
            setOf(Accion.VER_TODOS_LOS_REPORTES, Accion.GESTIONAR_REPORTE),
            accionesDe(PerfilUsuario.AGENTE)
        )
        assertFalse(PermisosPorRol.puede(PerfilUsuario.EJECUTIVO, Accion.GESTIONAR_REPORTE))
        assertFalse(PermisosPorRol.puede(PerfilUsuario.USUARIO_FINAL, Accion.GESTIONAR_REPORTE))
    }

    @Test
    fun `el usuario final nunca ve informacion interna (RNF-13)`() {
        val acciones = accionesDe(PerfilUsuario.USUARIO_FINAL)
        assertFalse(Accion.VER_COMPROMISOS_EMPRESA in acciones)
        assertFalse(Accion.VER_TODOS_LOS_REPORTES in acciones)
        assertFalse(Accion.VER_REPORTES_EMPRESA in acciones)
    }

    @Test
    fun `el ejecutivo ve los reportes de su empresa solo en lectura (RN-22)`() {
        assertTrue(PermisosPorRol.puede(PerfilUsuario.EJECUTIVO, Accion.VER_REPORTES_EMPRESA))
        assertFalse(PermisosPorRol.puede(PerfilUsuario.EJECUTIVO, Accion.VER_TODOS_LOS_REPORTES))
        assertFalse(PermisosPorRol.puede(PerfilUsuario.EJECUTIVO, Accion.VER_COMPROMISOS_PROPIOS))
    }

    @Test
    fun `sin perfil no se puede hacer nada`() {
        Accion.entries.forEach { assertFalse(PermisosPorRol.puede(null, it)) }
    }
}
