package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.pagoflex.app.core.profiles.model.PerfilUsuario

@Entity(
    tableName = "rol",
    indices = [
        Index(value = ["codigo"], unique = true)
    ]
)
data class Rol(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_rol")
    val idRol: Int = 0,

    // usuario_final, agente o ejecutivo
    val codigo: PerfilUsuario,
    val nombre: String,
    val descripcion: String? = null
)
