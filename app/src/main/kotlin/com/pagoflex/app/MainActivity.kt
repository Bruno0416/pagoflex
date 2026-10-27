package com.pagoflex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.features.auth.presentation.screens.AuthScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val db by lazy {
        LocalDatabase.obtenerInstancia(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            // 1. inicializar la base de datos
            withContext(Dispatchers.IO) {
                db.initDB()
            }

            // 2. mostrar la aplicación
            setContent {
                PagoFlexTheme {
                    AuthScreen(db = db)
                }
            }
        }
    }
}
