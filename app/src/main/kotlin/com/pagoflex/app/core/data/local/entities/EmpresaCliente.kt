package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// empresas que cobran a traves de pagoflex
@Entity(
    tableName = "empresa_cliente",
    foreignKeys = [
        ForeignKey(
            entity = Rubro::class,
            parentColumns = ["id_rubro"],
            childColumns = ["id_rubro"]
        )
    ],
    indices = [
        Index(value = ["codigo"], unique = true),
        Index(value = ["rut"], unique = true),
        Index(value = ["id_rubro"])
    ]
)
data class EmpresaCliente(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_empresa")
    val idEmpresa: Int = 0,

    val codigo: String,

    @ColumnInfo(name = "razon_social")
    val razonSocial: String,

    @ColumnInfo(name = "nombre_fantasia")
    val nombreFantasia: String,

    val rut: String,

    @ColumnInfo(name = "id_rubro")
    val idRubro: Int,

    @ColumnInfo(name = "correo_atencion")
    val correoAtencion: String,

    @ColumnInfo(name = "telefono_atencion")
    val telefonoAtencion: String,

    // recargo fijo en pesos chilenos, mayor o igual que cero
    @ColumnInfo(name = "recargo_atraso")
    val recargoAtraso: Int,

    val activa: Boolean
) {
    init {
        require(recargoAtraso >= 0)
    }
}
