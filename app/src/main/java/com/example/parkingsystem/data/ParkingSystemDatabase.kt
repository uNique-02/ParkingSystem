package com.example.parkingsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class ParkingSystemDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var Instance: ParkingSystemDatabase? = null

        fun getDatabase(context: Context): ParkingSystemDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ParkingSystemDatabase::class.java, "user_database").fallbackToDestructiveMigration()
                    .build().also {
                        Instance = it
                    }
            }
        }


    }


}