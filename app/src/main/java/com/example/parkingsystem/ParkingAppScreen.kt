package com.example.parkingsystem

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


enum class ParkingAppScreen() {
    WelcomePage, Login, Register, MapView
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingAppBar(
    canNavigateBack: Boolean, navigateUp: () -> Unit, modifier: Modifier = Modifier
) {
    TopAppBar(title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = "Back Button"
                    )
                }
            }
        })
}

@Composable
fun ParkingApp(
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = ParkingAppScreen.WelcomePage.name
    ) {
        composable(route = ParkingAppScreen.WelcomePage.name) {
            ScaffoldWrapper(
                canNavigateBack = false,
                navigateUp = { /* Implement back navigation */ }) {
                WelcomePage(navController = navController)
            }
        }

        composable(route = ParkingAppScreen.Login.name) {
            ScaffoldWrapper(
                canNavigateBack = false,
                navigateUp = { /* Implement back navigation */ }) {
                LoginScreen(onLogin = { _, _ -> }, navController = navController)
            }
        }

        composable(route = ParkingAppScreen.Register.name) {
            RegisterScreen(onLogin = { _, _ -> }, navController = navController)
        }

        composable(route = ParkingAppScreen.MapView.name) {
            ParkingAreaList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWrapper(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    content: @Composable (innerPadding: Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            ParkingAppBar(
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->
        Modifier.padding(innerPadding).let { paddingModifier ->
            content(paddingModifier)
        }
    }
}

@Composable
fun ParkingAreaList(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavController = rememberNavController(),
) {
    // Define the state of the search text
    var searchText by remember { mutableStateOf(" ") }

    // Use a Box to layer content
    Box() {
        // OSMDroidMapView as the background covering the whole screen
        OSMDroidMapView(
            modifier = Modifier.fillMaxHeight()
            // This ensures the map view covers the entire screen
        )

        // Column to hold the overlays
        Column(
            modifier = Modifier
                .fillMaxSize() // Allow the column to take the full space
                .padding(16.dp)
        ) {
            // Top section with title and search box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Parking Areas", fontSize = 16.sp
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0XFF101921))
                ) {
                    TextField(value = searchText,
                        onValueChange = { searchText = it },
                        textStyle = TextStyle(fontSize = 20.sp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                modifier = Modifier.clickable {
                                    // Handle the search icon click here
                                    // For example, perform a search operation based on searchText
                                    //getGeoPoint(searchText)
                                }
                            )
                        },
                        placeholder = { Text(text = "Search") })
                }
            }

            Spacer(modifier = Modifier.height(500.dp))

            // Sort and location section
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sort by: Distance")
                Text("UP Tacloban")
                Text("Tacloban Place")
            }

            // Parking area items
            ParkingAreaItem(title = "RTR Plaza", price = "FREE")
            ParkingAreaItem(title = "Nique'Residence", price = "P20/HR")

        }
    }
}

@Composable
fun OSMDroidMapView(modifier: Modifier = Modifier.fillMaxHeight()) {
    // Define a state to hold the MapView instance

    val context = LocalContext.current // Get current context
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    val MY_USER_AGENT = "com.example.osmbonuspack"

    // Initialize RoadManager with current context
    //val roadManager: RoadManager = OSRMRoadManager(context, MY_USER_AGENT)
    val taclobanGeoPoint = GeoPoint(11.2443, 125.0015)
    val location = remember { mutableStateOf<GeoPoint?>(taclobanGeoPoint) }

    val lastKnownLocation = remember { mutableStateOf<GeoPoint?>(taclobanGeoPoint) }

    //(roadManager as OSRMRoadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE)

    val poiMarkers = FolderOverlay()

    // Coroutine scope for background operations
    val coroutineScope = rememberCoroutineScope()

    val mapView = remember { mutableStateOf<MapView?>(null) }

    // Use a Box to layer the MapView and FloatingActionButton
    Box() {
        // Initialize and set up the MapView
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    initializeMap()
                    mapViewState.value = this

                    // Set up touch listener for handling map clicks
                    /*setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_UP) {
                            // Convert screen coordinates to GeoPoint
                            val clickGeoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
                            // Set the clicked point as the endpoint
                            endPoint.value = GeoPoint(clickGeoPoint)

                            // Launch a coroutine for network operations
                            coroutineScope.launch(Dispatchers.IO) {
                                // Add the clicked point as an endpoint to the waypoints
                                val waypoints = arrayListOf(taclobanGeoPoint)
                                endPoint.value?.let {
                                    waypoints.add(it)
                                }

                                // Get the road in a background thread
                                val road = roadManager.getRoad(waypoints)

                                // Switch to the main thread to update the UI
                                launch(Dispatchers.Main) {
                                    // Create road overlay and add it to the map
                                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                                    overlays.add(roadOverlay)
                                    invalidate() // Refresh the map
                                }
                            }
                        }
                        true // Return true to indicate the event was handled
                    }*/
                }
            },
            modifier = modifier.fillMaxSize()
        )

        // Function to remove all existing POI markers and overlays
        // Function to remove all existing POI markers and overlays
        // Define a function to remove all existing POI markers and overlays
        fun removeAllPOIMarkers(mapView: MapView?) {
            // Ensure that the map view is not null
            if (mapView == null) return

            // Remove all POI markers and overlays from the map
            mapView.overlays.clear()
            mapView.invalidate() // Refresh the map
        }

        fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
            // Calculate the distance between loc1 and loc2 in meters
            return loc1.distanceToAsDouble(loc2)
        }


        // FloatingActionButton for the "location" button
        FloatingActionButton(onClick = {
            // Access the map view instance
            val map = mapViewState.value

            Log.e("MainActivity", "Enter Floating Action Button")

            // Log the map instance
            Log.e("MainActivity", "Map: $map")

            // Access the MyLocationNewOverlay
            val locationOverlay =
                map?.overlays?.find { it is MyLocationNewOverlay } as? MyLocationNewOverlay
            if (locationOverlay == null) {
                // Log a message if MyLocationNewOverlay is null
                println("MyLocationNewOverlay instance is null")
                return@FloatingActionButton // Exit the click listener early if locationOverlay is null
            }

            // If the overlay exists and has a valid location, center the map on the hardcoded location
            map.controller.setCenter(taclobanGeoPoint)
            Marker(map).position = taclobanGeoPoint

            // Iterate through the overlays to find the startMarker
            for (overlay in map.overlays) {
                if (overlay is Marker) {
                    // If the Marker overlay is the startMarker you want to change
                    overlay.position = taclobanGeoPoint
                    break // Exit the loop once the marker is found and updated
                }
            }

            val locationHandler = LocationHandler(context)
            // Define the callback for location updates
            locationHandler.callback = object : LocationCallback {
                // In the callback function (onLocationUpdate)
                override fun onLocationUpdate(newLocation: GeoPoint) {
                    // Update the user's location
                    location.value = newLocation

                    // Remove all existing POI markers and overlays
                    removeAllPOIMarkers(mapViewState.value)

                    // Now add new POI markers and overlays based on the new location
                    coroutineScope.launch(Dispatchers.IO) {
                        val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
                        val pois = poiProvider.getPOICloseTo(location.value, "Parking", 50, 0.1)

                        withContext(Dispatchers.Main) {
                            val mapView = mapViewState.value
                            if (mapView != null) {
                                // Add new POI markers and overlays
                                var poiIcon = ContextCompat.getDrawable(
                                    context,
                                    R.drawable.marker_poi_default
                                )
                                val poiIconBitmap = (poiIcon as BitmapDrawable).bitmap

                                // Define the new width and height for the icon (change these to your desired dimensions)
                                val newWidth = 20 // e.g., 50 pixels
                                val newHeight = 30 // e.g., 50 pixels

                                // Resize the bitmap to the desired dimensions
                                val resizedPoiIconBitmap =
                                    Bitmap.createScaledBitmap(
                                        poiIconBitmap,
                                        newWidth,
                                        newHeight,
                                        true
                                    )

                                // Convert the resized bitmap back to a drawable
                                poiIcon = BitmapDrawable(context.resources, resizedPoiIconBitmap)

                                for (poi in pois) {
                                    val poiMarker = Marker(mapView)
                                    poiMarker.title = poi.mType
                                    Log.e("MainActivity", "POI type: ${poi.mType}")
                                    poiMarker.snippet = poi.mDescription
                                    Log.e(
                                        "MainActivity",
                                        "POI Description: ${poi.mDescription}"
                                    )
                                    poiMarker.position = poi.mLocation
                                    Log.e(
                                        "MainActivity",
                                        "POI Location: ${poi.mLocation.latitude}, ${poi.mLocation.longitude}"
                                    )
                                    poiMarker.icon = poiIcon

                                    poiMarkers.add(poiMarker)
                                }
                                // Add POI markers overlay to the map
                                mapView.overlays.add(poiMarkers)
                                mapView.invalidate()
                            }
                        }
                    }
                    // Update the user's location
                    location.value = newLocation

                    // Calculate the distance from the last known location
                    lastKnownLocation.value?.let { lastLocation ->
                        val distance = calculateDistance(lastLocation, newLocation)

                        // Define a threshold distance in meters (e.g., 50 meters)
                        val thresholdDistance = 5.0 // meters

                        // Animate to the new location if the distance exceeds the threshold
                        if (distance > thresholdDistance) {
                            mapViewState.value?.controller?.animateTo(newLocation, 18.0, 2000)
                        }
                    }

                    // Update the last known location to the current location
                    lastKnownLocation.value = newLocation
                }
            }
            locationHandler.startLocationUpdates()


            // Log the current map center
            val currentCenter = map.getMapCenter()
            Log.e(
                "MainActivity",
                "Location: ${currentCenter.latitude}, ${currentCenter.longitude}"
            )
        },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            content = {
                // Icon for the FloatingActionButton
                Icon(
                    imageVector = Icons.Default.LocationOn, // Choose the appropriate icon
                    contentDescription = "Show My Location"
                )
            })
    }


    mapViewState.value?.let { mapView ->
        val mapController: IMapController = mapView.controller
        val startPoint = GeoPoint(48.13, -1.63)

        mapController.setZoom(9.0)
        mapController.setCenter(startPoint)

        val startMarker = Marker(mapView)
        startMarker.setPosition(location.value)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(startMarker)
    }
}

fun MapView.initializeMap() {

    setMultiTouchControls(true)
    setTileSource(TileSourceFactory.MAPNIK)
    val startPoint = GeoPoint(12.8797, 121.7740)
    controller.setCenter(startPoint)
    controller.setZoom(6.0)

    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
    myLocationOverlay.enableMyLocation()
    myLocationOverlay.enableFollowLocation()
    overlays.add(myLocationOverlay)

    val mapListener = object : MapListener {
        override fun onScroll(event: ScrollEvent?): Boolean {
            return true
        }

        override fun onZoom(event: ZoomEvent?): Boolean {
            return false
        }
    }

    addMapListener(mapListener)
}

@Composable
fun ParkingAreaItem(title: String, price: String) {
    Row(
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title, modifier = Modifier.weight(0.7f)
        )

        Text(
            text = price, modifier = Modifier.weight(0.3f)
        )
    }
}


