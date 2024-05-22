package com.example.parkingsystem

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.parkingsystem.data.AppContainer
import com.example.parkingsystem.data.AppDataContainer
import com.example.parkingsystem.viewmodel.LoginViewModel
import com.example.parkingsystem.viewmodel.RegisterViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for RegisterViewModel
        initializer {
            val application = this.parkingSpaceApplication()
            RegisterViewModel(application.container.usersRepository)
        }

        // Initializer for LoginViewModel
        initializer {
            val application = this.parkingSpaceApplication()
            LoginViewModel(application.container.usersRepository)
        }

        // Add other initializers if needed
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.parkingSpaceApplication(): ParkingSpaceApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ParkingSpaceApplication)

