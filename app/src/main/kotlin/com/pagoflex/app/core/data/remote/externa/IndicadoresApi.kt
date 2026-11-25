package com.pagoflex.app.core.data.remote.externa

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

// api pública mindicador.cl: indicadores económicos de chile (no es parte de nuestro backend)
interface IndicadoresApi {
    @GET("api")
    suspend fun indicadores(): Response<IndicadoresDto>
}

data class IndicadoresDto(
    val fecha: String,
    val uf: IndicadorDto?,
    val dolar: IndicadorDto?,
)

data class IndicadorDto(
    val codigo: String,
    val nombre: String,
    @SerializedName("unidad_medida") val unidadMedida: String,
    val fecha: String,
    val valor: Double,
)
