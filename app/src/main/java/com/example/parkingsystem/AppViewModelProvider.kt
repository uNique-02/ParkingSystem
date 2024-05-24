package com.example.parkingsystem

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.parkingsystem.model.AddParkingSpaceViewModel
import com.example.parkingsystem.model.ProfileViewModel
import com.example.parkingsystem.viewmodel.LoginViewModel
import com.example.parkingsystem.viewmodel.RegisterViewModel

object AppViewModelProvider {
    fun provideFactory(context: Context) = viewModelFactory {
        // Initializer for RegisterViewModel
        initializer {
            val application = this.parkingSpaceApplication()
            RegisterViewModel(application.container.usersRepository, application.container.businessRepository, context)
        }

        // Initializer for LoginViewModel
        initializer {
            val application = this.parkingSpaceApplication()
            LoginViewModel(application.container.usersRepository, application.container.businessRepository, context)
        }

        initializer {
            val application = this.parkingSpaceApplication()
            ProfileViewModel(application.container.usersRepository, context)
        }

        initializer {
            val application = this.parkingSpaceApplication()
            AddParkingSpaceViewModel(application.container.usersRepository, context)
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

