package com.pagoflex.app.core.data.remote.externa

import kotlinx.coroutines.CancellationException
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

// valores del día para mostrar junto al resumen
data class IndicadoresDia(
    val uf: Double,
    val dolar: Double,
    val fecha: LocalDate,
)

// cliente propio, sin el token ni la url del gateway: así no se mezcla con nuestros microservicios
class IndicadoresRemoto(private val api: IndicadoresApi) {

    companion object {
        private const val URL = "https://mindicador.cl/"

        @Volatile
        private var instancia: IndicadoresRemoto? = null

        fun crear(): IndicadoresRemoto = instancia ?: synchronized(this) {
            instancia ?: IndicadoresRemoto(
                Retrofit.Builder()
                    .baseUrl(URL)
                    .client(
                        OkHttpClient.Builder()
                            .connectTimeout(8, TimeUnit.SECONDS)
                            .readTimeout(8, TimeUnit.SECONDS)
                            .build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(IndicadoresApi::class.java)
            ).also { instancia = it }
        }
    }

    // null si la api externa no responde: el resumen se muestra igual
    suspend fun delDia(): IndicadoresDia? = try {
        val respuesta = api.indicadores()
        val cuerpo = respuesta.body()
        val uf = cuerpo?.uf?.valor
        val dolar = cuerpo?.dolar?.valor
        if (respuesta.isSuccessful && uf != null && dolar != null) {
            IndicadoresDia(uf, dolar, OffsetDateTime.parse(cuerpo.uf.fecha).toLocalDate())
        } else {
            null
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}
