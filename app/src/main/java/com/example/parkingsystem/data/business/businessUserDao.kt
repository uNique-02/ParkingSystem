package com.example.parkingsystem.data.business

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parkingsystem.data.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface businessUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: businessUser)

    @Update
    suspend fun update(user: businessUser)


    @Delete
    suspend fun delete(user: businessUser)

    @Query("SELECT * from business WHERE businessUsername = :username")
    fun getUser(username: String): Flow<businessUser>

    @Query("SELECT * from business WHERE businessID = :businessID")
    fun getCurrentUser(businessID: Int): Flow<businessUser>

    @Query("SELECT * from business WHERE businessUsername = :username")
    fun getCurrentUserID(username: String): Flow<businessUser>

    @Query("SELECT * from business ORDER BY businessID ASC")
    fun getAllUsers(): Flow<List<businessUser>>

}