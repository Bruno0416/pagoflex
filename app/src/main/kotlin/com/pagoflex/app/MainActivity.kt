package com.pagoflex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import com.pagoflex.app.core.navigation.AppNavegacion
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.network.auth.model.PerfilUsuario
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
                    if (perfil == PerfilUsuario.USUARIO_FINAL) {
                        AppNavegacion(cerrarSesion)
                    } else {
                        Text("Pantalla agente y ejecutivo pendientes")
                    }
                }
            }
        }
    }
}
