package com.pagoflex.app.features.auth.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.navigation.AppNavegacion
import com.pagoflex.app.core.profiles.SesionPreferences
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.presentation.viewmodel.AuthViewModel
import com.pagoflex.app.features.auth.presentation.viewmodel.LoginViewModel
import com.pagoflex.app.features.auth.presentation.viewmodel.RecuperarContrasenaViewModel
import com.pagoflex.app.features.auth.presentation.viewmodel.RegisterViewModel

@Composable
fun AuthScreen(db: LocalDatabase) {
    // ---------- dependencias ----------
    val context = LocalContext.current.applicationContext
    val authRepository = remember(db) { AuthRepository(db) }
    val sesionPreferences = remember(context) { SesionPreferences(context) }

    // ---------- view models ----------
    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AuthViewModel(authRepository, sesionPreferences)
            }
        }
    )

    val loginViewModel: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                LoginViewModel(authRepository)
            }
        }
    )

    val registerViewModel: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RegisterViewModel(authRepository)
            }
        }
    )

    val recuperarViewModel: RecuperarContrasenaViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RecuperarContrasenaViewModel(authRepository)
            }
        }
    )

    // ---------- estado de autenticación ----------
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val sesion = authState.sesion

    when {
        authState.cargando -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        sesion == null -> {
            // ---------- navegación auth ----------
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        viewModel = loginViewModel,
                        crearSesion = authViewModel::crearSesion,
                        irARegistro = {
                            navController.navigate("registro")
                        },
                        irARecuperar = {
                            navController.navigate("recuperar")
                        }
                    )
                }

                composable("registro") {
                    RegisterScreen(
                        viewModel = registerViewModel,
                        volverAlLogin = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("recuperar") {
                    RecuperarContrasenaScreen(
                        viewModel = recuperarViewModel,
                        volverAlLogin = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }

        else -> {
            AppNavegacion(
                perfilUsuario = sesion.perfil,
                usuarioId = sesion.usuarioId,
                db = db
            )
        }
    }
}
