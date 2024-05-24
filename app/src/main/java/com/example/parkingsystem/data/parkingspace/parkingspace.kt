package com.example.parkingsystem.data.parkingspace

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parkingspace")
data class parkingspace(
    @PrimaryKey(autoGenerate = true)
    val spaceID: Int = 0,
    val longitude: String,
    val latitude: String,
    val name: String,
    val address: String,
    val businessID: Int,
    val rate: Double,
    val isAvailable: Boolean,
    val spaceAvailble: Int,
    val capacity: Int
)