package com.example.parkingsystem.data

import android.content.Context
import android.util.Log
import com.example.parkingsystem.data.business.BusinessRepository
import com.example.parkingsystem.data.business.OfflineBusinessRepository
import com.example.parkingsystem.data.user.OfflineUsersRepository
import com.example.parkingsystem.data.user.UsersRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val usersRepository: UsersRepository
    val businessRepository: BusinessRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context,
) : AppContainer {
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
    override val businessRepository: BusinessRepository by lazy {
        OfflineBusinessRepository(ParkingSystemDatabase.getDatabase(context).businessUserDao())
    }
}
