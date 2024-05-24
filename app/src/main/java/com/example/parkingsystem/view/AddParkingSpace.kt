package com.example.parkingsystem.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.parkingsystem.AppViewModelProvider
import com.example.parkingsystem.data.parkingspace.ParkingspaceRepository
import com.example.parkingsystem.data.parkingspace.parkingspace
import com.example.parkingsystem.model.AddParkingSpaceViewModel
import com.example.parkingsystem.model.ProfileViewModel

@Composable
fun AddParkingSpaceScreen(
    navController: NavController
) {
    val viewModel: AddParkingSpaceViewModel = viewModel(
        factory = AppViewModelProvider.provideFactory(
            LocalContext.current
        )
    )

    val name by viewModel.name.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val address by viewModel.address.collectAsState()
    val rate by viewModel.rate.collectAsState()
    val isAvailable by viewModel.isAvailable.collectAsState()
    val spaceAvailable by viewModel.spaceAvailable.collectAsState()
    val capacity by viewModel.capacity.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        TextField(
            value = name,
            onValueChange = { viewModel.setName(it) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        // Add more TextFields for other fields like address, longitude, latitude, etc.

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val parkingSpace = parkingspace(
                    name = name,
                    address = address,
                    longitude = longitude,
                    latitude = latitude,
                    businessID = 1,
                    rate = rate?: 0.0,
                    isAvailable = isAvailable,
                    spaceAvailble = spaceAvailable ?: 0,
                    capacity = capacity ?: 0
                )
                viewModel.addParkingSpace(parkingSpace)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Parking Space")
        }
    }
}
