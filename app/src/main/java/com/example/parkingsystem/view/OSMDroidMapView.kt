package com.example.parkingsystem.view

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.parkingsystem.LocationCallback
import com.example.parkingsystem.LocationHandler
import com.example.parkingsystem.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.concurrent.CopyOnWriteArrayList
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.osmdroid.bonuspack.location.POI
import kotlin.collections.ArrayList
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun OSMDroidMapView(modifier: Modifier = Modifier.fillMaxHeight()) {
    val context = LocalContext.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    val taclobanGeoPoint = GeoPoint(11.2443, 125.0015)
    val location = remember { mutableStateOf<GeoPoint?>(taclobanGeoPoint) }
    val lastKnownLocation = remember { mutableStateOf<GeoPoint?>(taclobanGeoPoint) }
    val poiMarkers = FolderOverlay()
    val coroutineScope = rememberCoroutineScope()
    var hasZoomedToUserLocation by remember { mutableStateOf(false) }

    val locationHandler = remember {
        LocationHandler(context).apply {
            callback = object : LocationCallback {
                override fun onLocationUpdate(newLocation: GeoPoint) {
                    location.value = newLocation
                    if (mapViewState.value != null) {
                        coroutineScope.launch {
                            updateMapViewLocation(mapViewState, location, lastKnownLocation, hasZoomedToUserLocation)
                            removeAllPOIMarkers(mapViewState.value)
                            addPOIMarkers(context, mapViewState, location, poiMarkers, coroutineScope)
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        locationHandler.startLocationUpdates()
        onDispose {
            locationHandler.stopLocationUpdates()
        }
    }

    Box {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    initializeMap()
                    mapViewState.value = this
                }
            },
            modifier = modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = {
                hasZoomedToUserLocation = false // Reset zoom flag
                onLocationButtonClick(context, mapViewState, location, lastKnownLocation, poiMarkers, coroutineScope, hasZoomedToUserLocation)
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            content = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Show My Location"
                )
            }
        )
    }

    mapViewState.value?.let { mapView ->
        val startMarker = Marker(mapView)
        startMarker.setPosition(location.value)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(startMarker)
    }
}

fun MapView.initializeMap() {
    setMultiTouchControls(true)
    setTileSource(TileSourceFactory.MAPNIK)
    controller.setCenter(GeoPoint(12.8797, 121.7740))
    controller.setZoom(6.0)

    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
    myLocationOverlay.enableMyLocation()
    //myLocationOverlay.enableFollowLocation()
    overlays.add(myLocationOverlay)

    addMapListener(object : MapListener {
        override fun onScroll(event: ScrollEvent?) = true
        override fun onZoom(event: ZoomEvent?) = false
    })
}

fun onLocationButtonClick(
    context: android.content.Context,
    mapViewState: MutableState<MapView?>,
    location: MutableState<GeoPoint?>,
    lastKnownLocation: MutableState<GeoPoint?>,
    poiMarkers: FolderOverlay,
    coroutineScope: CoroutineScope,
    hasZoomedToUserLocation: Boolean
) {

    val map = mapViewState.value ?: return
    val locationOverlay = map.overlays.find { it is MyLocationNewOverlay } as? MyLocationNewOverlay

    if (locationOverlay == null) {
        Log.e("onLocationButtonClick", "MyLocationNewOverlay instance is null")
        return
    }

    val userLocation = locationOverlay.myLocation
    if (userLocation != null) {
        location.value = userLocation
        removeAllPOIMarkers(mapViewState.value)
        addPOIMarkers(context, mapViewState, location, poiMarkers, coroutineScope)
        updateMapViewLocation(mapViewState, location, lastKnownLocation, hasZoomedToUserLocation)
        map.controller.animateTo(userLocation, 18.0, 2000)
        Log.i("onLocationButtonClick", "Zooming to user location: ${userLocation.latitude}, ${userLocation.longitude}")
    } else {
        Log.e("onLocationButtonClick", "User location is null")
    }
}

fun removeAllPOIMarkers(mapView: MapView?) {
    mapView?.overlays?.clear()
    mapView?.invalidate()
}

fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
    return loc1.distanceToAsDouble(loc2)
}

fun clearRouteOverlays(mapView: MapView) {
    (mapView.overlays as? CopyOnWriteArrayList<Overlay>)?.removeIf { it is Polyline }
        ?: run {
            mapView.overlays.filterNot { it is Polyline } as MutableList<Overlay>
        }
}

fun calculateDistanceBetweenPoints(loc1: GeoPoint, loc2: GeoPoint): Double {
    val result = FloatArray(1)
    android.location.Location.distanceBetween(
        loc1.latitude,
        loc1.longitude,
        loc2.latitude,
        loc2.longitude,
        result
    )
    return result[0].toDouble()
}
fun addPOIMarkers(
    context: android.content.Context,
    mapViewState: MutableState<MapView?>,
    location: MutableState<GeoPoint?>,
    poiMarkers: FolderOverlay,
    coroutineScope: CoroutineScope,
    maxDistanceMeters: Double = 10000.0 // Maximum distance in meters (10 kilometers)
) {
    val roadManager = OSRMRoadManager(context, "OSMBonusPackTutoUserAgent")
    coroutineScope.launch(Dispatchers.IO) {
        val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
        val maxDistanceDegrees = metersToDegrees(maxDistanceMeters, location.value?.latitude ?: 0.0)
        val pois = poiProvider.getPOICloseTo(location.value, "Parking", 50, maxDistanceDegrees)

        withContext(Dispatchers.Main) {
            mapViewState.value?.let { mapView ->
                val poiIcon = ContextCompat.getDrawable(context, R.drawable.marker_poi_default) as BitmapDrawable
                val resizedPoiIcon = BitmapDrawable(
                    context.resources,
                    Bitmap.createScaledBitmap(poiIcon.bitmap, 20, 30, true)
                )

                // Calculate distances and create a map of POI to distance
                val distances = pois.associateWith { calculateDistanceBetweenPoints(it.mLocation, location.value!!) }

                // Sort POIs based on distance from user's location
                val sortedPOIs = pois.sortedBy { distances[it] }.take(50)

                poiMarkers.items.clear()
                sortedPOIs.forEach { poi ->
                    val poiMarker = Marker(mapView)
                    poiMarker.title = poi.mType
                    poiMarker.snippet = poi.mDescription
                    poiMarker.subDescription = "Distance: " + String.format("%.2f", (distances[poi] ?: 0.0) / 1000) + " km"
                    poiMarker.position = poi.mLocation
                    poiMarker.icon = resizedPoiIcon

                    poiMarker.setOnMarkerClickListener { marker, mapView ->
                        // Show a bottom sheet dialog with POI details
                        showPOIBottomSheetDialog(context, poi, distances[poi] ?: 0.0)

                        // Perform route calculation in a background thread
                        AsyncTask.execute {
                            try {
                                val waypoints = ArrayList<GeoPoint>().apply {
                                    add(location.value!!)
                                    add(poi.mLocation)
                                }

                                val road: Road = roadManager.getRoad(waypoints)
                                val roadOverlay: Polyline = RoadManager.buildRoadOverlay(road)

                                // Run UI operations on the main thread
                                mapView.post {
                                    clearRouteOverlays(mapView) // Clear previous route overlays
                                    mapView.overlays.add(roadOverlay)
                                    mapView.invalidate() // Refresh the map view
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        true // Event is consumed
                    }

                    poiMarkers.add(poiMarker)
                }

                mapView.overlays.add(poiMarkers)
                mapView.invalidate()
            }
        }
    }
}

private var bottomSheetDialog: BottomSheetDialog? = null
fun showPOIBottomSheetDialog(context: Context, poi: POI, distance: Double) {

    if (bottomSheetDialog == null || bottomSheetDialog?.isShowing == false) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_poi, null)
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog?.setContentView(dialogView)

        val titleTextView = dialogView.findViewById<TextView>(R.id.poi_title)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.poi_description)
        val distanceTextView = dialogView.findViewById<TextView>(R.id.poi_distance)

        titleTextView.text = poi.mType
        descriptionTextView.text = poi.mDescription
        distanceTextView.text = String.format("Distance: %.2f km", distance / 1000)

        bottomSheetDialog?.show()
    }
}

fun metersToDegrees(meters: Double, latitude: Double): Double {
    // Convert latitude from degrees to radians
    val latRadians = Math.toRadians(latitude)

    // One degree of latitude is approximately 111 kilometers
    // (at the equator; it decreases as you move towards the poles)
    val metersPerDegreeLat = 111000.0

    // Calculate the equivalent distance in degrees
    val degrees = meters / (metersPerDegreeLat * Math.cos(latRadians))

    return degrees
}

fun updateMapViewLocation(
    mapViewState: MutableState<MapView?>,
    location: MutableState<GeoPoint?>,
    lastKnownLocation: MutableState<GeoPoint?>,
    hasZoomedToUserLocation: Boolean
) {
    val newLocation = location.value ?: return
    val mapView = mapViewState.value ?: return

    lastKnownLocation.value?.let { lastLocation ->

        val distance = calculateDistance(lastLocation, newLocation)
        if (distance > 5.0 && !hasZoomedToUserLocation) {
            mapView.controller.animateTo(newLocation, 18.0, 2000)
            hasZoomedToUserLocation.not()
        }
    }

    lastKnownLocation.value = newLocation
}

@Composable
fun ParkingAreaItem(title: String, price: String) {
    Row(
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(0.7f))
        Text(text = price, modifier = Modifier.weight(0.3f))
    }
}
