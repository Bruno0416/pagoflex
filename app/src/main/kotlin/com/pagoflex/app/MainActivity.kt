package com.pagoflex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pagoflex.app.core.navigation.AppNavegacion
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.features.auth.presentation.screens.AuthScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(applicationContext)
        enableEdgeToEdge()

        setContent {
            PagoFlexTheme {
                AuthScreen { perfil, cerrarSesion ->
                    AppNavegacion(perfil, cerrarSesion)
                }
            }
        }
    }
}
