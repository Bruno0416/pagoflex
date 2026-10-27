package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

// personas que ingresan a la app; los datos propios de cada rol son opcionales
@Entity(
    tableName = "usuario",
    foreignKeys = [
        ForeignKey(
            entity = Rol::class,
            parentColumns = ["id_rol"],
            childColumns = ["id_rol"]
        ),
        ForeignKey(
            entity = Comuna::class,
            parentColumns = ["id_comuna"],
            childColumns = ["id_comuna"]
        ),
        ForeignKey(
            entity = Area::class,
            parentColumns = ["id_area"],
            childColumns = ["id_area"]
        ),
        ForeignKey(
            entity = EmpresaCliente::class,
            parentColumns = ["id_empresa"],
            childColumns = ["id_empresa"]
        ),
        ForeignKey(
            entity = PreguntaRecuperacion::class,
            parentColumns = ["id_pregunta_recuperacion"],
            childColumns = ["id_pregunta_recuperacion"]
        )
    ],
    indices = [
        Index(value = ["rut"], unique = true),
        Index(value = ["correo"], unique = true),
        Index(value = ["id_rol"]),
        Index(value = ["id_comuna"]),
        Index(value = ["id_area"]),
        Index(value = ["id_empresa"]),
        Index(value = ["id_pregunta_recuperacion"])
    ]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    // un rol por usuario
    @ColumnInfo(name = "id_rol")
    val idRol: Int,

    // rut con dígito verificador válido
    val rut: String,

    val nombres: String,

    val apellidos: String,

    val correo: String,

    val telefono: String? = null,

    @ColumnInfo(defaultValue = "1")
    val activo: Boolean = true,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: LocalDateTime,

    // solo para usuario final
    @ColumnInfo(name = "id_comuna")
    val idComuna: Int? = null,

    // solo para usuario final: fecha de ingreso a pagoflex
    @ColumnInfo(name = "fecha_incorporacion")
    val fechaIncorporacion: LocalDate? = null,

    // solo para usuario final, si se implementan avisos
    @ColumnInfo(name = "avisos_activos")
    val avisosActivos: Boolean? = null,

    // solo para usuario final: 1, 3 o 5 dias
    @ColumnInfo(name = "dias_anticipacion")
    val diasAnticipacion: Int? = null,

    // solo para agentes: soporte o comercial
    @ColumnInfo(name = "id_area")
    val idArea: Int? = null,

    // solo para ejecutivos: empresa a la que pertenecen
    @ColumnInfo(name = "id_empresa")
    val idEmpresa: Int? = null,

    // solo para ejecutivos
    val cargo: String? = null,

    // acceso y recuperación
    @ColumnInfo(name = "password_hash")
    val passwordHash: String? = null,

    @ColumnInfo(name = "id_pregunta_recuperacion")
    val idPreguntaRecuperacion: Int? = null,

    @ColumnInfo(name = "respuesta_recuperacion_hash")
    val respuestaRecuperacionHash: String? = null,

    // se configura posteriormente desde la feature de perfil
    val avatar: ByteArray? = null
) {
    init {
        require(diasAnticipacion == null || diasAnticipacion in listOf(1, 3, 5))
    }
}
