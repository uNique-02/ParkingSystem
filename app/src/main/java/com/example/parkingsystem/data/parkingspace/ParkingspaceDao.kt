package com.example.parkingsystem.data.parkingspace

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parkingsystem.data.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingspaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParkingSpace(parkingSpace: parkingspace)

    @Update
    suspend fun update(parkingSpace: parkingspace)

    @Query("SELECT * FROM parkingspace")
    fun getAllParkingSpaces(): Flow<List<parkingspace>>

    @Query("SELECT * FROM parkingspace WHERE spaceID = :id")
    fun getParkingSpaceById(id: Int): Flow<parkingspace>

    @Query("SELECT * FROM parkingspace WHERE name = :name")
    fun getParkingSpaceByName(name: String): Flow<parkingspace>

    @Query("DELETE FROM parkingspace WHERE spaceID = :id")
    suspend fun deleteParkingSpaceById(id: Int)

    @Query("DELETE FROM parkingspace WHERE name = :name")
    suspend fun deleteParkingSpaceByName(name: String)

    @Query("DELETE FROM parkingspace")
    suspend fun deleteAllParkingSpaces()
}
