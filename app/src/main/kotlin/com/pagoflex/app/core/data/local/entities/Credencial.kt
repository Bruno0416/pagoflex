package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "credencial",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Credencial(
    @PrimaryKey
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
)