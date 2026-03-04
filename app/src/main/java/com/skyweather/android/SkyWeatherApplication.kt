package com.skyweather.android

import android.app.Application
import android.content.Context

class SkyWeatherApplication : Application() {

    companion object {
        const val TOKEN = "F4CzNk3kE1oFUC25"
        @Suppress("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}