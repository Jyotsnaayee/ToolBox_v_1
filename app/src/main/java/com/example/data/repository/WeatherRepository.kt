package com.example.data.repository

import com.example.data.remote.WeatherResponse
import com.example.data.remote.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WeatherRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherService::class.java)

    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = service.getCurrentWeather(lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
