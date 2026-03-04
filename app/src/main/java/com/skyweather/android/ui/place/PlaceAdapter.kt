package com.skyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyweather.android.R
import com.skyweather.android.logic.model.Place
import com.skyweather.android.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>):
RecyclerView.Adapter<PlaceAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName:TextView = view.findViewById(R.id.placeName)
        val placeAddress:TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,
            parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            if(activity is WeatherActivity){
                activity.weatherActivityBinding.drawerLayout.closeDrawers()
                val split = place.name.split(" ")
                activity.viewModel.placeName = split[split.size - 1]
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.locationLng = place.location.lng
                activity.refreshWeather()
                // 清空输入框
                fragment.binding.searchPlaceEdit.text.clear()
            }else{
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    val split = place.name.split(" ")
                    putExtra("place_name", split[split.size - 1])
                }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    override fun onBindViewHolder(holder: PlaceAdapter.ViewHolder, position: Int) {
        val place = placeList[position]
        val split = place.name.split(" ")
        holder.placeName.text = split[split.size - 1]
        holder.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size
}