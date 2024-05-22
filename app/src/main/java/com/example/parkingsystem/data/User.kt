package com.example.parkingsystem.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userID: Int = 0,
    val fName: String,
    val lName: String,
    val address: String,
    val pNumber: String,
    val username: String,
    val password: String
)
