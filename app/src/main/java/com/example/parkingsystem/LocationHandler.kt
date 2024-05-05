package com.example.parkingsystem

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint

// Callback interface for passing location updates
interface LocationCallback {
    fun onLocationUpdate(location: GeoPoint)
}

class LocationHandler(private val context: Context) {

    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationListener: LocationListener
    private val REQUEST_LOCATION_PERMISSION = 1001

    init {
        // Define a location listener
        // Define a location listener
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                // Convert the location to a GeoPoint and pass it to the callback
                val userLocation = GeoPoint(location.latitude, location.longitude)
                callback?.onLocationUpdate(userLocation)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Handle provider status changes if needed
            }

            override fun onProviderEnabled(provider: String) {
                // Handle provider enable events if needed
            }

            override fun onProviderDisabled(provider: String) {
                // Handle provider disable events if needed
            }

        }
    }

    // Define a variable to hold the callback
    var callback: LocationCallback? = null

    // Function to start location updates
    fun startLocationUpdates() {
        // Check for location permissions
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, "android.permission.ACCESS_FINE_LOCATION"
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, "android.permission.ACCESS_COARSE_LOCATION"
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000, // Minimum time interval between updates in milliseconds
                10f, // Minimum distance between updates in meters
                locationListener
            )
        } else {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    // Function to stop location updates
    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    // Handle permission result in the activity
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start location updates
                    startLocationUpdates()
                } else {
                    // Permission denied, handle appropriately
                    Log.d("LocationHandler", "Location permission denied")
                }
            }
        }
    }
}
