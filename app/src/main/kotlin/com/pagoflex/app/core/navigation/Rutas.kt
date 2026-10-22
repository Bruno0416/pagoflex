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

    data object ReportarProblema : Ruta("compromiso/{folio}/reportar") {
        fun crear(folio: String) = "compromiso/$folio/reportar"
    }


    // Agente
    data object BandejaReportes : Ruta("agente/reportes")

    // --- rutas secundarias ---
    data object DetalleReporte : Ruta("agente/reporte/{numero}") {
        fun crear(numero: String) = "agente/reporte/$numero"
    }

    // Ejecutivo
    data object ResumenCobranza : Ruta("ejecutivo/resumen")
    data object CompromisosEmpresa : Ruta("ejecutivo/compromisos")
    data object RegistrarCompromiso : Ruta("ejecutivo/registrar")

    // --- rutas secundarias ---
    data object DetalleCompromisoEmpresa : Ruta("ejecutivo/compromiso/{folio}") {
        fun crear(folio: String) = "ejecutivo/compromiso/$folio"
    }

}
