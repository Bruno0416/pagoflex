package com.pagoflex.app.core.navigation

sealed class Ruta(val ruta: String) {
    // Usuario final
    data object Home : Ruta("home")
    data object Historial : Ruta("historial")
    data object Ayuda : Ruta("ayuda")
    data object Avisos : Ruta("avisos")

    // --- rutas secundarias ---
    data object DetalleCompromiso : Ruta("compromiso/{id}") {
        fun crear(id: String) = "compromiso/$id"
    }


    // Agente
    data object BandejaReportes : Ruta("agente/reportes")

    // --- rutas secundarias ---

    // Ejecutivo
    data object ResumenCobranza : Ruta("ejecutivo/resumen")
    data object CompromisosEmpresa : Ruta("ejecutivo/compromisos")
    data object RegistrarCompromiso : Ruta("ejecutivo/registrar")

    // --- rutas secundarias ---

}