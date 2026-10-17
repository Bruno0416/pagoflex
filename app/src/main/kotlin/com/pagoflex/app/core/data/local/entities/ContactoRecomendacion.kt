package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

// contacto opcional de la organizacion, solo con autorizacion
@Entity(
    tableName = "contacto_recomendacion",
    foreignKeys = [
        ForeignKey(
            entity = Recomendacion::class,
            parentColumns = ["id_recomendacion"],
            childColumns = ["id_recomendacion"]
        )
    ]
)
data class ContactoRecomendacion(
    @PrimaryKey
    @ColumnInfo(name = "id_recomendacion")
    val idRecomendacion: Int,

    val nombre: String,

    val correo: String? = null,

    val telefono: String? = null,

    // solo se admite un contacto con autorizacion
    @ColumnInfo(name = "autoriza_contacto")
    val autorizaContacto: Boolean
) {
    init {
        require(autorizaContacto)
    }
}
