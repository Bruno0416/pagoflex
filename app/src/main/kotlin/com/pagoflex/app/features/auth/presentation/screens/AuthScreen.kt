package com.pagoflex.app.features.auth.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.network.auth.model.PerfilUsuario
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.presentation.viewmodel.LoginViewModel
import com.pagoflex.app.features.auth.presentation.viewmodel.RecuperarContrasenaViewModel
import com.pagoflex.app.features.auth.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    contenidoAutenticado: @Composable (PerfilUsuario, () -> Unit) -> Unit
) {
    val repository = remember { AuthRepository() }
    val sessionManager = RetrofitClient.sessionManager
    val sesion by sessionManager.sesion.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val loginViewModel: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer { LoginViewModel(repository) }
        }
    )

    val registerViewModel: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer { RegisterViewModel(repository) }
        }
    )

    val recuperarViewModel: RecuperarContrasenaViewModel = viewModel(
        factory = viewModelFactory {
            initializer { RecuperarContrasenaViewModel(repository) }
        }
    )

    val perfil = sesion?.perfil
    if (perfil == null) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    viewModel = loginViewModel,
                    irARegistro = { navController.navigate("registro") },
                    irARecuperar = { navController.navigate("recuperar") }
                )
            }
            composable("registro") {
                RegisterScreen(
                    viewModel = registerViewModel,
                    volverAlLogin = { navController.popBackStack() }
                )
            }
            composable("recuperar") {
                RecuperarContrasenaScreen(
                    viewModel = recuperarViewModel,
                    volverAlLogin = { navController.popBackStack() }
                )
            }
        }
    } else {
        contenidoAutenticado(perfil) {
            scope.launch {
                sessionManager.cerrarSesion()
                loginViewModel.limpiarFormulario()
            }
        }
    }
}
