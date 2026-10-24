package com.pagoflex.app.core.data.local

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.withWriteTransaction
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.pagoflex.app.core.data.local.converters.FechaConverters
import com.pagoflex.app.core.data.local.dao.AnulacionDao
import com.pagoflex.app.core.data.local.dao.CompromisoDao
import com.pagoflex.app.core.data.local.dao.EmpresaClienteDao
import com.pagoflex.app.core.data.local.dao.PagoDao
import com.pagoflex.app.core.data.local.dao.PreguntaRecuperacionDao
import com.pagoflex.app.core.data.local.dao.RecomendacionDao
import com.pagoflex.app.core.data.local.dao.ReconocimientoDao
import com.pagoflex.app.core.data.local.dao.ReferenciaDao
import com.pagoflex.app.core.data.local.dao.ReporteDao
import com.pagoflex.app.core.data.local.dao.UsuarioDao
import com.pagoflex.app.core.data.local.entities.Anulacion
import com.pagoflex.app.core.data.local.entities.Area
import com.pagoflex.app.core.data.local.entities.Canal
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.core.data.local.entities.Comuna
import com.pagoflex.app.core.data.local.entities.ContactoRecomendacion
import com.pagoflex.app.core.data.local.entities.EmpresaCliente
import com.pagoflex.app.core.data.local.entities.EstadoCompromiso
import com.pagoflex.app.core.data.local.entities.EstadoRecomendacion
import com.pagoflex.app.core.data.local.entities.EstadoReporte
import com.pagoflex.app.core.data.local.entities.HistorialRecomendacion
import com.pagoflex.app.core.data.local.entities.MedioPago
import com.pagoflex.app.core.data.local.entities.MotivoAnulacion
import com.pagoflex.app.core.data.local.entities.MotivoReporte
import com.pagoflex.app.core.data.local.entities.Pago
import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion
import com.pagoflex.app.core.data.local.entities.Recomendacion
import com.pagoflex.app.core.data.local.entities.Reconocimiento
import com.pagoflex.app.core.data.local.entities.Reporte
import com.pagoflex.app.core.data.local.entities.Rol
import com.pagoflex.app.core.data.local.entities.Rubro
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.data.local.entities.UsuarioReconocimiento

@Database(
    entities = [
        Comprobante::class,
        PreguntaRecuperacion::class,
        Rol::class,
        Usuario::class,
        EmpresaCliente::class,
        Compromiso::class,
        Anulacion::class,
        Pago::class,
        Reporte::class,
        Recomendacion::class,
        ContactoRecomendacion::class,
        HistorialRecomendacion::class,
        Reconocimiento::class,
        UsuarioReconocimiento::class,
        Comuna::class,
        Area::class,
        Rubro::class,
        EstadoCompromiso::class,
        MotivoAnulacion::class,
        Canal::class,
        MedioPago::class,
        MotivoReporte::class,
        EstadoReporte::class,
        EstadoRecomendacion::class
    ],
    version = LocalDatabase.VERSION_DB,
    exportSchema = true
)
@ColumnTypeConverters(FechaConverters::class)
abstract class LocalDatabase : RoomDatabase() {
    // ---------- registro de daos ----------
    abstract fun preguntaRecuperacionDao(): PreguntaRecuperacionDao
    abstract fun referenciasDao(): ReferenciaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun empresaClienteDao(): EmpresaClienteDao
    abstract fun compromisoDao(): CompromisoDao
    abstract fun pagoDao(): PagoDao
    abstract fun reporteDao(): ReporteDao
    abstract fun anulacionDao(): AnulacionDao
    abstract fun recomendacionDao(): RecomendacionDao
    abstract fun reconocimientoDao(): ReconocimientoDao

    // ---------- instancia compartida ----------
    companion object {
        const val VERSION_DB = 4
        private const val NOMBRE_DB = "pagoflex.db"

        private var instancia: LocalDatabase? = null

        @Synchronized
        fun obtenerInstancia(context: Context): LocalDatabase {
            if (instancia == null) {
                instancia = Room.databaseBuilder<LocalDatabase>(
                    context.applicationContext,
                    NOMBRE_DB
                )
                    .setDriver(AndroidSQLiteDriver())
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
            }

            return checkNotNull(instancia)
        }
    }


    // ---------- inicialización db ----------
    suspend fun initDB() {
        withWriteTransaction {
            if (usuarioDao().contarUsuarios() > 0) return@withWriteTransaction
            CargaInicial(this@LocalDatabase).cargarDatos()
        }
    }
}

