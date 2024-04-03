package com.example.restaurantfinder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place



class MarkerInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        // 1. Get tag
        val place = marker.tag as? Place ?: return null

        // 2. Inflate view and set title, address and rating
        val view = LayoutInflater.from(context).inflate(R.layout.marker_info_contents, null)
        view.findViewById<TextView>(R.id.text_view_title).text = place.name
        view.findViewById<TextView>(R.id.text_view_address).text = place.address
        view.findViewById<RatingBar>(R.id.rating_bar).rating = place.rating.toFloat()
        // if GPT summary fails aka I run out of credit it will default to the name and review of the 1st person fetched by the places api
        // if GPT summary works it adds a review into the first (0th) element and is handled by the same logic
        view.findViewById<TextView>(R.id.text_view_author).text = place.reviews[0].authorAttribution.name
        view.findViewById<TextView>(R.id.text_view_description).text = place.reviews[0].text

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        // Return null to indicate that the default window (white bubble) should be used
        return null
    }





    companion object {
        private const val TAG = "MarkerInfoWindowAdapter"
       }
}