package com.example.parkingsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.parkingsystem.data.business.businessUser
import com.example.parkingsystem.data.business.businessUserDao
import com.example.parkingsystem.data.user.User
import com.example.parkingsystem.data.user.UserDao

@Database(entities = [User::class, businessUser::class], version = 2, exportSchema = false)
abstract class ParkingSystemDatabase : RoomDatabase() {
    abstract fun userDao(

    ): UserDao
    abstract fun businessUserDao(): businessUserDao // Add this line

    companion object {
        @Volatile
        private var Instance: ParkingSystemDatabase? = null

        fun getDatabase(context: Context): ParkingSystemDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ParkingSystemDatabase::class.java, "user_database")
                    .fallbackToDestructiveMigration()
                    .build().also {
                        Instance = it
                    }
            }
        }
    }
}
