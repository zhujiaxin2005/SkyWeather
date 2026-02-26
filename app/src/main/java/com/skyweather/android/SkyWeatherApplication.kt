package com.skyweather.android

import android.app.Application
import android.content.Context

class SkyWeatherApplication : Application() {

    companion object {
        const val TOKEN = "zuYlZrh2mxzlBa0s"
        @Suppress("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}