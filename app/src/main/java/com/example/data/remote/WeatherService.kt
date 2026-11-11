package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "current_weather") val currentWeather: CurrentWeather?
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    @Json(name = "weathercode") val weatherCode: Int,
    val time: String
)

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true
    ): WeatherResponse
}
