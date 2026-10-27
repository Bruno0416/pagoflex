package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Area
import com.pagoflex.app.core.data.local.entities.Canal
import com.pagoflex.app.core.data.local.entities.Comuna
import com.pagoflex.app.core.data.local.entities.EstadoCompromiso
import com.pagoflex.app.core.data.local.entities.EstadoRecomendacion
import com.pagoflex.app.core.data.local.entities.EstadoReporte
import com.pagoflex.app.core.data.local.entities.MedioPago
import com.pagoflex.app.core.data.local.entities.MotivoAnulacion
import com.pagoflex.app.core.data.local.entities.MotivoReporte
import com.pagoflex.app.core.data.local.entities.Rol
import com.pagoflex.app.core.data.local.entities.Rubro

@Dao
interface ReferenciaDao {
    @Insert
    suspend fun insertarRoles(rol: List<Rol>): List<Long>

    @Query(
        """
        SELECT id_rol FROM rol WHERE codigo = :codigo
    """
    )
    suspend fun obtenerIdRolPorCodigo(codigo: String): Int?


    @Insert
    suspend fun insertarComunas(comuna: List<Comuna>): List<Long>

    @Insert
    suspend fun insertarAreas(area: List<Area>): List<Long>

    @Insert
    suspend fun insertarRubros(rubro: List<Rubro>): List<Long>

    @Insert
    suspend fun insertarEstadoCompromisos(estadoCompromiso: List<EstadoCompromiso>): List<Long>

    @Insert
    suspend fun insertarMotivoAnulacions(motivoAnulacion: List<MotivoAnulacion>): List<Long>

    @Insert
    suspend fun insertarCanales(canal: List<Canal>): List<Long>

    @Insert
    suspend fun insertarMedioPagos(medioPago: List<MedioPago>): List<Long>

    @Insert
    suspend fun insertarMotivoReportes(motivoReporte: List<MotivoReporte>): List<Long>

    @Insert
    suspend fun insertarEstadoReportes(estadoReporte: List<EstadoReporte>): List<Long>

    @Insert
    suspend fun insertarEstadoRecomendacions(estadoRecomendacion: List<EstadoRecomendacion>): List<Long>
    // ---------- referencias para pago ----------
    @Query("SELECT id_estado_compromiso FROM estado_compromiso WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerIdEstadoCompromiso(nombre: String): Int?

    @Query("SELECT id_canal FROM canal WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerIdCanal(nombre: String): Int?

    @Query("SELECT id_medio_pago FROM medio_pago WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerIdMedioPago(nombre: String): Int?
}
