package com.skyweather.android.logic.network

import com.skyweather.android.SkyWeatherApplication
import com.skyweather.android.logic.model.DailyResponse
import com.skyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.6/${SkyWeatherApplication.TOKEN}/{location}/realtime")
    fun getRealtimeWeather(@Path("location") location: String): Call<RealtimeResponse>

    @GET("v2.6/${SkyWeatherApplication.TOKEN}/{location}/daily.json")
    fun getDailyWeather(@Path("location") location: String): Call<DailyResponse>
}