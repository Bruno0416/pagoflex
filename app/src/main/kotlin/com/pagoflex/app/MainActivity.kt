package com.pagoflex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.navigation.AppNavegacion
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            // 1. inicializar la base de datos
            withContext(Dispatchers.IO) {
                val db = LocalDatabase.obtenerInstancia(applicationContext)
                db.initDB()
            }
            // 2. mostrar app
            setContent {
                PagoFlexTheme {
                    AppNavegacion()
                }
            }
        }
    }


}