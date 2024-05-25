package com.example.restaurantfinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aallam.openai.client.OpenAI
import com.android.volley.BuildConfig
import com.example.restaurantfinder.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.restaurantfinder.PermissionUtils.isPermissionGranted
import com.example.restaurantfinder.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity(),
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener,
    OnMapReadyCallback {

    private var permissionDenied = false
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: use viewbinding method in future:
        // binding = ActivityMapsBinding.inflate(layoutInflater)
        // setContentView(binding.root)
        setContentView(R.layout.my_location_demo)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        //initialize places sdk
        // TODO: retrieve API key through secrets.gradle
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),"PLACESAPIKEY");
        }

        BuildConfig.BUILD_TYPE

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)



        val travel_sprite = LatLng(49.35,-123.12)
        val zoomLevel = 12.0f
        val radius = 5000 // 5km radius

        val  cameraUpdate = CameraUpdateFactory.newLatLngZoom(travel_sprite, zoomLevel)


       // mMap.addMarker(MarkerOptions().position(travel_sprite).title("My Avatar").icon(bicycleIcon))
        mMap.moveCamera(cameraUpdate)



        // Set custom info window adapter
        mMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
        // Set up click handler
       // mMap.setOnInfoWindowClickListener(MarkerWindowClickHandler(this))
        enableMyLocation()


//        markerInfoWindow.setOnButtonClickListener {
//            val summary = "hello"
//            markerInfoWindow.updateInfoWindowContent(mMap.selectedMarker, summary)
//
//        }



    }





    private fun drawSquare(lat: Double, lng: Double, dx: Double, dy: Double) {
        // Instantiates a new Polygon object and adds points to define a rectangle
        val polygonOptions = PolygonOptions()
            .add(
                LatLng(lat - dy, lng + dx),
                LatLng(lat + dy, lng + dx),
                LatLng(lat + dy, lng - dx),
                LatLng(lat - dy, lng - dx)
            )

        // Get back the mutable Polygon
        val polygon: Polygon = mMap.addPolygon(polygonOptions)
    }

    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.spritecolor)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_sprite_black_24dp, color)
    }

    private fun searchNearbyRestaurants(lat: Double, lng: Double, dx: Double, dy: Double){
        val placesClient = Places.createClient(this)
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING,Place.Field.USER_RATINGS_TOTAL, Place.Field.REVIEWS)

        val southWest = LatLng(lat - dy, lng - dx)
        val northEast = LatLng(lat + dy, lng + dx)
        val searchByTextRequest = SearchByTextRequest.builder("Spicy Vegetarian Food", placeFields)
            .setMaxResultCount(5)
            .setLocationRestriction(RectangularBounds.newInstance(southWest, northEast)).build()

        placesClient.searchByText(searchByTextRequest)
            .addOnSuccessListener { response ->
                val places: List<Place> = response.places

                places.forEach { place ->
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .title(place.name)
                            .position(place.latLng)

                    )
                    // sendChatRequest is a Suspend function (takes long to run)
                    // have to run in when the cpus free
                    CoroutineScope(Dispatchers.Main).launch {
                        val reviewSummary = GPTsummarizer.sendChatRequest(place)
                        reviewSummary?.let { Log.i(TAG, "GPT3.5 $it") }
                        //update marker
                        reviewSummary?.let { GPTsummarizer.updatePlaceData(marker, it) }
                    }

                    marker?.tag = place

                }
            }

    }


    // location functions

    private fun enableMyLocation() {

        // [START maps_check_location_permission]
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
        // [END maps_check_location_permission]
    }
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        //TODO get user location and feed it into searcNearbyRestaurants funtction
        val goldenswan_lat = 49.2367
        val goldenswan_lng = -123.0654
        val goldenswan_dx = 0.1
        val goldenswan_dy = 0.1
        //drawSquare(goldenswan_lat,goldenswan_lng,goldenswan_dx,goldenswan_dy)
        searchNearbyRestaurants(goldenswan_lat,goldenswan_lng,goldenswan_dx,goldenswan_dy)
        return false
    }
    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
            // [END_EXCLUDE]
        }
    }
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }
    companion object {
        private const val TAG = "MapsActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        // TODO: retrieve API key through secrets.gradle
        private val GPTsummarizer = OpenAISummarizer(OpenAI("OPENAISDKKEY"))

    }

}


