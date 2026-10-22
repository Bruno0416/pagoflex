package com.pagoflex.app.features.cobranza.presentation.model

import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.core.rules.ResumenCobranza
import com.pagoflex.app.features.cobranza.data.EjecutivoSesion
import com.pagoflex.app.features.cobranza.data.PersonaEncontrada
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.YearMonth

// resumen de la cobranza del mes (HU-12, RF-20)
data class ResumenCobranzaUiState(
    val cargando: Boolean = true,
    val ejecutivo: EjecutivoSesion? = null,
    val mes: YearMonth = YearMonth.now(),
    val resumen: ResumenCobranza = ResumenCobranza(YearMonth.now()),

    // errores
    val error: Int? = null,
)

// compromisos de la empresa con filtros (RF-17)
data class CompromisosEmpresaUiState(
    val cargando: Boolean = true,
    val ejecutivo: EjecutivoSesion? = null,
    val mes: YearMonth = YearMonth.now(),
    // nulo muestra todos
    val filtro: EstadoCompromiso? = null,
    val compromisos: List<CompromisoEmpresa> = emptyList(),

    // errores
    val error: Int? = null,
)

// detalle y anulación (RF-17, RF-19)
data class DetalleCompromisoUiState(
    val cargando: Boolean = true,
    val compromiso: CompromisoEmpresa? = null,
    val reporte: ReporteDelCompromiso? = null,

    // hoja de anulación
    val hojaAnulacionVisible: Boolean = false,
    val motivo: MotivoAnulacion? = null,
    val comentario: String = "",
    val anulando: Boolean = false,

    // mensajes y errores
    val mensaje: Int? = null,
    val errorAnulacion: Int? = null,
    val error: Int? = null,
)

// registro de un compromiso (HU-11, RF-18)
data class RegistrarCompromisoUiState(
    val formulario: FormularioCompromiso = FormularioCompromiso(),
    val personaEncontrada: PersonaEncontrada? = null,
    val rutValido: Boolean = false,
    val cargando: Boolean = false,
    val registrado: Compromiso? = null,

    // errores
    val errores: Map<CampoCompromiso, Int> = emptyMap(),
    val errorGeneral: Int? = null,
) {
    // con rut válido y sin persona se piden sus datos básicos (RN-20)
    val pedirDatosPersona: Boolean
        get() = rutValido && personaEncontrada == null
}
