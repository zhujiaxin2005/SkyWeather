package com.skyweather.android.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SkyWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather("$lng,$lat").await()

    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather("$lng,$lat").await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("SkyWeatherNetwork", "Response success: $body")
                        continuation.resume(body)
                    }
                    else {
                        val errorCode = response.code()
                        val errorMessage = response.message()
                        val errorBody = response.errorBody()?.string()
                        Log.e("SkyWeatherNetwork", "Response failed - Code: $errorCode, Message: $errorMessage, ErrorBody: $errorBody")
                        continuation.resumeWithException(
                            RuntimeException("response body is null. Code: $errorCode, Message: $errorMessage, Body: $errorBody")
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e("SkyWeatherNetwork", "Request failure", t)
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}