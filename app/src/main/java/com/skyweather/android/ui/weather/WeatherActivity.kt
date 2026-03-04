package com.skyweather.android.ui.weather

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.skyweather.android.R
import com.skyweather.android.databinding.ForecastBinding
import com.skyweather.android.databinding.LifeIndexBinding
import com.skyweather.android.databinding.NowBinding
import com.skyweather.android.logic.model.DailyResponse
import com.skyweather.android.logic.model.RealtimeResponse
import com.skyweather.android.logic.model.Weather
import com.skyweather.android.logic.model.getSky

class WeatherActivity : AppCompatActivity() {

    private lateinit var nowBinding: NowBinding
    private lateinit var forecastBinding: ForecastBinding
    private lateinit var lifeIndexBinding: LifeIndexBinding

    private val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.weatherLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        nowBinding = NowBinding.bind(findViewById(R.id.now_layout))
        forecastBinding = ForecastBinding.bind(findViewById(R.id.forecast_layout))
        lifeIndexBinding = LifeIndexBinding.bind(findViewById(R.id.life_index_layout))
        scrollView = findViewById(R.id.weatherLayout)

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this) {
            val weather = it.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        }

        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {
        nowBinding.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        //填充now.xml布局中的数据
        nowBinding.currentTemp.text = "${realtime.temperature.toInt()} ℃"
        nowBinding.currentSky.text = getSky(realtime.skycon).info
        nowBinding.currentAqi.text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        nowBinding.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast.xml布局中的数据
        forecastBinding.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = layoutInflater.inflate(R.layout.forecast_item,
                forecastBinding.forecastLayout, false)
            view.findViewById<TextView>(R.id.dateInfo).text = skycon.date.substring(0, 10)
            view.findViewById<ImageView>(R.id.skyIcon).setImageResource(getSky(skycon.value).icon)
            view.findViewById<TextView>(R.id.skyInfo).text = getSky(skycon.value).info
            view.findViewById<TextView>(R.id.temperatureInfo).text =
                "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            forecastBinding.forecastLayout.addView(view)
        }
        //填充life_index.xml布局中的数据
        lifeIndexBinding.coldRiskText.text = daily.lifeIndex.coldRisk[0].desc
        lifeIndexBinding.carWashingText.text = daily.lifeIndex.carWashing[0].desc
        lifeIndexBinding.ultravioletText.text = daily.lifeIndex.ultraviolet[0].desc
        lifeIndexBinding.dressingText.text = daily.lifeIndex.dressing[0].desc
        scrollView.visibility = View.VISIBLE
    }
}