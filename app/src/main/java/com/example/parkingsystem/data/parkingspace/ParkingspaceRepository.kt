package com.example.parkingsystem.data.parkingspace

import com.example.parkingsystem.data.user.User
import com.example.parkingsystem.data.user.UserDao
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface ParkingspaceRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllParkingStream(): Flow<List<parkingspace>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getParkingStream(username: String): Flow<parkingspace?>

    /**
     * Insert item in the data source
     */
    suspend fun insertParkingspace(parkingspace: parkingspace)

    /**
     * Delete item from the data source
     */
    suspend fun deleteParkingSpace(parkingspace: parkingspace)

    /**
     * Update item in the data source
     */
    suspend fun updateParkingSpace(parkingspace: parkingspace)

    suspend fun isParkingSpceValid(name: String): Boolean
}

class OfflineParkingspaceRepository(private val parkingspaceDao: ParkingspaceDao) : ParkingspaceRepository {
    override fun getAllParkingStream(): Flow<List<parkingspace>> =  parkingspaceDao.getAllParkingSpaces()

    override fun getParkingStream(name: String): Flow<parkingspace?> = parkingspaceDao.getParkingSpaceByName(name)

    override suspend fun insertParkingspace(parkingspace: parkingspace) = parkingspaceDao.insertParkingSpace(parkingspace)

    override suspend fun deleteParkingSpace(parkingspace: parkingspace) = parkingspaceDao.deleteParkingSpaceByName(parkingspace.name)

    override suspend fun updateParkingSpace(parkingspace: parkingspace) = parkingspaceDao.update(parkingspace)

    override suspend fun isParkingSpceValid(name: String): Boolean {
                val parkingspace = parkingspaceDao.getParkingSpaceByName(name).firstOrNull()
                return parkingspace?.name == name
            }
    }