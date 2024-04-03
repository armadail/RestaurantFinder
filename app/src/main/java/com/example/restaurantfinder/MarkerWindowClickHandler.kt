package com.example.restaurantfinder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerWindowClickHandler(private val context: Context) : GoogleMap.OnInfoWindowClickListener {
    override fun onInfoWindowClick(marker: Marker){
        Log.i(TAG,"Maker info Has been clicked")
        Log.i(TAG,marker.toString())
        // Update the info window content of the clicked marker
        marker.snippet = "Button clicked"
    }
     companion object {
        private const val TAG = "MarkerClickHandler"

    }


}
