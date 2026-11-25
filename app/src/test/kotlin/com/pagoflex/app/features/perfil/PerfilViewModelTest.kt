package com.pagoflex.app.features.perfil

import com.pagoflex.app.R
import com.pagoflex.app.features.perfil.data.PerfilRepository
import com.pagoflex.app.features.perfil.presentation.viewmodel.PerfilViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PerfilViewModelTest {

    private lateinit var apiFalsa: FakePerfilApi
    private lateinit var repo: PerfilRepository
    private lateinit var viewmodel: PerfilViewModel

    @Before
    fun preparar() {
        apiFalsa = FakePerfilApi()
        repo = PerfilRepository(apiFalsa)
    }

    private fun cargarPerfil(sinConexion: Boolean = false) {
        apiFalsa.sinConexion = sinConexion

        runBlocking {
            viewmodel = PerfilViewModel(repo, this)
        }
    }

    @Test
    fun iniciarEdicion_sinPerfil_noHabilitaEdicion() {
        // 1. preparar perfil sin conexión
        cargarPerfil(sinConexion = true)

        // 2. comprobar estado inicial
        assertNull(viewmodel.uiState.value.perfil)

        // 3. intentar iniciar edición
        viewmodel.iniciarEdicion()

        // 4. guardar estado
        val state = viewmodel.uiState.value

        // 5. validar estado resultante
        assertFalse(state.editando)
        assertNull(state.perfil)
    }

    @Test
    fun cambiarContrasena_contrasenaRepetida_rechazaCambio() {
        val contrasenaActual = "Demo123!"

        // 1. cargar perfil
        cargarPerfil()
        assertNotNull(viewmodel.uiState.value.perfil)

        // 2. abrir formulario de cambio de contraseña
        viewmodel.abrirCambioContrasena()
        assertTrue(viewmodel.uiState.value.cambiandoContrasena)

        // 3. intentar cambiar contraseña a la actual
        viewmodel.cambiarContrasena(
            contrasenaActual,
            contrasenaActual,
            contrasenaActual
        )

        // 4. guardar estado
        val state = viewmodel.uiState.value

        // 5. asegurar mensaje de error
        assertEquals(
            R.string.perfil_contrasena_diferente,
            state.errorNuevaContrasena
        )
    }

     @Test
     fun cancelarEdicion_restauraDatosOriginales() {
         // 1. cargar perfil
         cargarPerfil()
         assertNotNull(viewmodel.uiState.value.perfil)
         val perfilOriginal = viewmodel.uiState.value.perfil

         // 2. iniciar edición
         viewmodel.iniciarEdicion()
         viewmodel.setNombres("pepito")
         viewmodel.setApellidos("perez")
         viewmodel.setCorreo("pepito.perez@correo.ejemplo")

         // 3. confirmar que cambiaron los valores antes de cancelar
         assertTrue(viewmodel.uiState.value.editando)
         assertEquals("pepito", viewmodel.uiState.value.nombres)
         assertEquals("perez", viewmodel.uiState.value.apellidos)
         assertEquals("pepito.perez@correo.ejemplo", viewmodel.uiState.value.correo)

         // 4. cancelar edicion
         viewmodel.cancelarEdicion()

         // 5. guardar estado
         val state = viewmodel.uiState.value

         // 6. comprobar que se restauraron los datos originales
         assertEquals(perfilOriginal?.nombres, state.nombres)
         assertEquals(perfilOriginal?.apellidos, state.apellidos)
         assertEquals(perfilOriginal?.correo, state.correo)
         assertFalse(state.editando)
     }
}