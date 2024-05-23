package com.example.parkingsystem.data.business

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business")
data class businessUser(
    @PrimaryKey(autoGenerate = true)
    val businessID: Int = 0,
    val businessName: String,
    val businessAddress: String,
    val businessNumber: String,
    val businessUsername: String,
    val businessPassword: String
)
