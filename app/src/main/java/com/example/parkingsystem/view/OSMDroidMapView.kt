package com.example.parkingsystem.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkingsystem.AppViewModelProvider
import com.example.parkingsystem.LocationCallback
import com.example.parkingsystem.LocationHandler
import com.example.parkingsystem.R
import com.example.parkingsystem.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OSMDroidMapView(modifier: Modifier = Modifier.fillMaxHeight(), sheetState: SheetState) {
    val context = LocalContext.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    val taclobanGeoPoint = GeoPoint(11.2443, 125.0015)
    val location = remember { mutableStateOf<GeoPoint?>(taclobanGeoPoint) }
    val lastKnownLocation = remember { mutableStateOf<GeoPoint?>(taclobanGeoPoint) }
    val poiMarkers = FolderOverlay()
    val coroutineScope = rememberCoroutineScope()
    var hasZoomedToUserLocation by remember { mutableStateOf(false) }

    val viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.provideFactory(
        LocalContext.current))

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
            onClick = { hasZoomedToUserLocation = false // Reset zoom flag
                Log.e("LocationButtonClicked", "Location Button Clicked. has zoom: " + hasZoomedToUserLocation)
                onLocationButtonClick(context, mapViewState, location, lastKnownLocation, poiMarkers, coroutineScope, hasZoomedToUserLocation, viewModel, sheetState) },
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
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

@OptIn(ExperimentalMaterial3Api::class)
fun onLocationButtonClick(
    context: android.content.Context,
    mapViewState: MutableState<MapView?>,
    location: MutableState<GeoPoint?>,
    lastKnownLocation: MutableState<GeoPoint?>,
    poiMarkers: FolderOverlay,
    coroutineScope: CoroutineScope,
    hasZoomedToUserLocation: Boolean,
    viewModel: LoginViewModel,
    sheetState: SheetState
) {
    val map = mapViewState.value

    if (!hasZoomedToUserLocation) {
        Log.e("LocationButtonClicked", "UpdateMapViewLocation $hasZoomedToUserLocation")
        map?.controller?.animateTo(location.value, 18.0, 2000)
        hasZoomedToUserLocation.not()
    }


    val locationOverlay = map?.overlays?.find { it is MyLocationNewOverlay } as? MyLocationNewOverlay
    if (locationOverlay == null) {
        println("MyLocationNewOverlay instance is null")
        return
    }

    map.overlays.filterIsInstance<Marker>().firstOrNull()?.position = GeoPoint(11.2443, 125.0015)
    val locationHandler = LocationHandler(context).apply {
        callback = object : LocationCallback {
            override fun onLocationUpdate(newLocation: GeoPoint) {
                location.value = newLocation
                removeAllPOIMarkers(mapViewState.value)
                addPOIMarkers(context, mapViewState, location, poiMarkers, coroutineScope, viewModel, sheetState)
                updateMapViewLocation(mapViewState, location, lastKnownLocation, hasZoomedToUserLocation)
            }
        }
    }
    locationHandler.startLocationUpdates()
    Log.e("MainActivity", "Location: ${map.getMapCenter().latitude}, ${map.getMapCenter().longitude}")
}

fun removeAllPOIMarkers(mapView: MapView?) {
    mapView?.overlays?.clear()
    mapView?.invalidate()
}

fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
    return loc1.distanceToAsDouble(loc2)
}

// Clear existing overlays before adding new ones
fun clearRouteOverlays(mapView: MapView) {
    (mapView.overlays as? CopyOnWriteArrayList<Overlay>)?.removeIf { it is Polyline }
        ?: run {
           mapView.overlays.filterNot { it is Polyline } as MutableList<Overlay>
        }
}


@OptIn(ExperimentalMaterial3Api::class)
fun addPOIMarkers(
    context: android.content.Context,
    mapViewState: MutableState<MapView?>,
    location: MutableState<GeoPoint?>,
    poiMarkers: FolderOverlay,
    coroutineScope: CoroutineScope,
    viewModel: LoginViewModel,
    sheetState: SheetState
) {
    val roadManager = OSRMRoadManager(context, "OSMBonusPackTutoUserAgent")

    coroutineScope.launch(Dispatchers.IO) {
        val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
        val pois = poiProvider.getPOICloseTo(location.value, "Parking", 50, 0.1)
        withContext(Dispatchers.Main) {
            mapViewState.value?.let { mapView ->
                val poiIcon = ContextCompat.getDrawable(context, R.drawable.marker_poi_default) as BitmapDrawable
                val resizedPoiIcon = BitmapDrawable(
                    context.resources,
                    Bitmap.createScaledBitmap(poiIcon.bitmap, 20, 30, true)
                )

                pois.forEach { poi ->
                    val poiMarker = Marker(mapView)
                    poiMarker.title = poi.mType
                    poiMarker.snippet = poi.mDescription
                    poiMarker.subDescription = "Distance: " + String.format("%.2f", (poi.mLocation.distanceToAsDouble(mapView.mapCenter)) / 1000) + " km"
                    poiMarker.position = poi.mLocation
                    poiMarker.icon = resizedPoiIcon

                    poiMarker.setOnMarkerClickListener { marker, mapView ->
                        // Toggle description visibility
                        /*if (marker.isInfoWindowShown) {
                            marker.closeInfoWindow()
                        } else {
                            marker.showInfoWindow()
                        }*/
                        coroutineScope.launch {
                            viewModel.setPoiAddress(poi.mDescription)
                            viewModel.setDistance(poiMarker.subDescription)
                            viewModel.toggleBottomSheet()
                            sheetState.show()
                        }

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

fun updateMapViewLocation(
    mapViewState: MutableState<MapView?>,
    location: MutableState<GeoPoint?>,
    lastKnownLocation: MutableState<GeoPoint?>,
    hasZoomedToUserLocation: Boolean
) {
    val newLocation = location.value ?: return
    val mapView = mapViewState.value ?: return

    lastKnownLocation.value?.let { lastLocation ->
        //val distance = calculateDistance(lastLocation, newLocation)
        if (!hasZoomedToUserLocation) {
            Log.e("LocationButtonClicked", "UpdateMapViewLocation $hasZoomedToUserLocation")
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

