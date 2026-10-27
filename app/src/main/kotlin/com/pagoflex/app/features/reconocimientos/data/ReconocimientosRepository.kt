package com.pagoflex.app.features.reconocimientos.data

import androidx.room3.withWriteTransaction
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.data.local.entities.Reconocimiento
import com.pagoflex.app.core.data.local.entities.UsuarioReconocimiento
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReconocimientosRepository(private val db: LocalDatabase) {
    private companion object {
        const val PRIMER_PAGO = "Primer pago a tiempo"
        const val TRES_MESES = "Tres meses al día"
    }

    // ---------- evaluación y consulta ----------
    suspend fun evaluarYObtener(usuarioId: Int): List<ReconocimientoObtenidoRow> {
        evaluar(usuarioId)
        return db.reconocimientoDao().obtenerDelUsuario(usuarioId)
    }

    suspend fun evaluar(usuarioId: Int, hoy: LocalDate = LocalDate.now()) {
        if (usuarioId <= 0) return
        db.withWriteTransaction {
            val dao = db.reconocimientoDao()
            // 1. asegurar el catálogo en bases nuevas y existentes
            dao.insertarCatalogoSiNoExiste(
                Reconocimiento(0, PRIMER_PAGO, "Tu primer compromiso pagado a tiempo")
            )
            dao.insertarCatalogoSiNoExiste(
                Reconocimiento(0, TRES_MESES, "Tres meses seguidos con tus pagos al día")
            )

            // 2. consultar todos los compromisos y sus pagos
            val registros = db.compromisoDao().obtenerCompromisosConPago(usuarioId)
            val ahora = LocalDateTime.now()
            val primero = registros
                .filter { it.estado.trim().lowercase() !in setOf("anulado", "en revisión", "en revision") }
                .filter { it.fechaPago?.toLocalDate()?.let { fecha -> !fecha.isAfter(it.fechaVencimiento) } == true }
                .minByOrNull { checkNotNull(it.fechaPago) }

            // 3. dar el primer pago puntual sin cambiar logros anteriores
            if (primero != null) {
                val id = checkNotNull(dao.obtenerIdPorNombre(PRIMER_PAGO))
                dao.darSiNoExiste(
                    UsuarioReconocimiento(
                        usuarioId, id, ahora,
                        "Pago ${primero.folio} del ${checkNotNull(primero.fechaPago).toLocalDate()}"
                    )
                )
            }

            // 4. dar una racha de tres meses cerrados
            CalculoRacha.buscarTresMeses(registros, hoy)?.let { meses ->
                val id = checkNotNull(dao.obtenerIdPorNombre(TRES_MESES))
                val formato = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-CL"))
                dao.darSiNoExiste(
                    UsuarioReconocimiento(
                        usuarioId, id, ahora,
                        "Pagos al día de ${meses.first().format(formato)} a ${meses.last().format(formato)}"
                    )
                )
            }
        }
    }
}
