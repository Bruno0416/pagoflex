package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime
import java.time.LocalDate

// obligacion de pago de un usuario final con una empresa cliente
@Entity(
    tableName = "compromiso",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario_final"]
        ),
        ForeignKey(
            entity = EmpresaCliente::class,
            parentColumns = ["id_empresa"],
            childColumns = ["id_empresa"]
        ),
        ForeignKey(
            entity = EstadoCompromiso::class,
            parentColumns = ["id_estado_compromiso"],
            childColumns = ["id_estado_compromiso"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_ejecutivo_registro"]
        )
    ],
    indices = [
        Index(value = ["folio"], unique = true),
        Index(value = ["id_usuario_final", "id_empresa", "concepto", "numero_cuota"], unique = true),
        Index(value = ["id_empresa"]),
        Index(value = ["id_estado_compromiso"]),
        Index(value = ["id_ejecutivo_registro"])
    ]
)
data class Compromiso(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_compromiso")
    val idCompromiso: Int,

    val folio: String,

    // deudor asociado, siempre con rol usuario final
    @ColumnInfo(name = "id_usuario_final")
    val idUsuarioFinal: Int,

    @ColumnInfo(name = "id_empresa")
    val idEmpresa: Int,

    val concepto: String,

    // nulo cuando no corresponde a un pago en cuotas
    @ColumnInfo(name = "numero_cuota")
    val numeroCuota: Int? = null,

    @ColumnInfo(name = "total_cuotas")
    val totalCuotas: Int? = null,

    // monto en pesos chilenos, mayor que cero
    val monto: Int,

    @ColumnInfo(name = "fecha_vencimiento")
    val fechaVencimiento: LocalDate,

    @ColumnInfo(name = "id_estado_compromiso")
    val idEstadoCompromiso: Int,

    // copia del recargo vigente al momento de vencer
    @ColumnInfo(name = "recargo_aplicado")
    val recargoAplicado: Int,

    @ColumnInfo(name = "fecha_cambio_estado")
    val fechaCambioEstado: LocalDateTime,

    // nulo si fue cargado directamente por pagoflex
    @ColumnInfo(name = "id_ejecutivo_registro")
    val idEjecutivoRegistro: Int? = null,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: LocalDateTime
) {
    init {
        require(monto > 0)
        require(recargoAplicado >= 0)
        require((numeroCuota == null && totalCuotas == null) || (numeroCuota != null && totalCuotas != null && numeroCuota > 0 && totalCuotas > 0 && numeroCuota <= totalCuotas))
    }
}
