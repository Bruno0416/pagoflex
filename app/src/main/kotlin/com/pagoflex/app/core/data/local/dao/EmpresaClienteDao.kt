package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.EmpresaCliente

@Dao
interface EmpresaClienteDao {
    @Insert
    suspend fun insertarEmpresaCliente(empresaCliente: EmpresaCliente): Long

    @Insert
    suspend fun insertarEmpresasCliente(empresasCliente: List<EmpresaCliente>): List<Long>

    @Query("SELECT * FROM empresa_cliente WHERE id_empresa = :empresaId LIMIT 1")
    suspend fun obtenerPorId(empresaId: Int): EmpresaCliente?

    @Query("SELECT id_empresa FROM empresa_cliente WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerIdPorCodigo(codigo: String): Int?
}
