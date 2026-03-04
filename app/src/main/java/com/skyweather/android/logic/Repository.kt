package com.skyweather.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.skyweather.android.logic.dao.PlaceDao
import com.skyweather.android.logic.model.Place
import com.skyweather.android.logic.model.Weather
import com.skyweather.android.logic.network.SkyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

object Repository {

    /*fun searchPlaces(query: String) = liveData(Dispatchers.IO){
        val result = try {
            val placeResponse = SkyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }*/

    fun searchPlaces(query: String) = fire{
        val placeResponse = SkyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    /*fun refreshWeather(lng: String, lat: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val deferredRealtime = async {
                    SkyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    SkyWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                    Result.success(weather)
                }else {
                    Result.failure(
                        RuntimeException("realtime response status is ${realtimeResponse.status}" +
                        "daily response status is ${dailyResponse.status}"))
                }
            }
        }catch (e: Exception){
            Result.failure(e)
        }
        emit(result)
    }*/

    fun refreshWeather(lng: String, lat: String) = fire{
        Log.d("Repository", "refreshWeather called with lng=$lng, lat=$lat")
        coroutineScope {
            val deferredRealtime = async {
                SkyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SkyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            Log.d("Repository", realtimeResponse.toString())
            val dailyResponse = deferredDaily.await()
            Log.d("Repository", dailyResponse.toString())
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            }else {
                val error = RuntimeException("realtime response status is ${realtimeResponse.status}," +
                        " daily response status is ${dailyResponse.status}")
                Log.e("Repository", "API status error", error)
                Result.failure(error)
            }
        }
    }

    private fun <T> fire(context: CoroutineContext = Dispatchers.IO, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            }catch (e: Exception){
                Log.d("Repository",e.toString())
                Result.failure(e)
            }
            Log.d("Repository",result.toString())
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}