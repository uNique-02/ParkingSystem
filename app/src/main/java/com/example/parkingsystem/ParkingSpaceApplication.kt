package com.example.parkingsystem

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.parkingsystem.data.AppContainer
import com.example.parkingsystem.data.AppDataContainer

class ParkingSpaceApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        Log.d("ParkingSpaceApplication", "onCreate() called")

        if (::container.isInitialized) {
            Log.d("ParkingSpaceApplication", "Container is not null")
        } else {
            Log.e("ParkingSpaceApplication", "Container is null")
        }
    }

    companion object {
        private var instance: ParkingSpaceApplication? = null

        fun getAppContainer(context: Context): AppContainer {
            return (context.applicationContext as ParkingSpaceApplication).container
        }
    }

}