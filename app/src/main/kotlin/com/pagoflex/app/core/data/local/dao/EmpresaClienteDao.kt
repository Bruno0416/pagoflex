package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.EmpresaCliente

@Dao
interface EmpresaClienteDao {
    @Insert
    suspend fun insertarEmpresaCliente(empresaCliente: EmpresaCliente): Long

    @Insert
    suspend fun insertarEmpresasCliente(empresasCliente: List<EmpresaCliente>): List<Long>
}
