package com.example.parkingsystem.data

import android.content.Context
import android.util.Log

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val usersRepository: UsersRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    init {
        // Logging to verify whether the context is null or not
        if (context == null) {
            Log.e("AppDataContainer", "Context is null!")
        } else {
            Log.d("AppDataContainer", "Context received: $context")
        }
    }

    /**
     * Implementation for [UsersRepository]
     */
    override val usersRepository: UsersRepository by lazy {
        OfflineUsersRepository(ParkingSystemDatabase.getDatabase(context).userDao())
    }
}
