package com.pagoflex.app.features.historial.data

import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Comprobante

class HistorialRepository(private val db: LocalDatabase) {

    suspend fun obtenerHistorial(usuarioId: Int): List<Comprobante> =
        db.pagoDao().obtenerHistorial(usuarioId)

    suspend fun obtenerDetalleCompromiso(numeroComprobante: String, usuarioId: Int) =
        db.pagoDao().obtenerDetalleComprobante(numeroComprobante, usuarioId)
}
