package com.example.parkingsystem.data.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parkingsystem.data.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)


    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * from users WHERE username = :username")
    fun getUser(username: String): Flow<User>

    @Query("SELECT * from users WHERE userID = :userID")
    fun getCurrentUser(userID: Int): Flow<User>

    @Query("SELECT * from users WHERE userID = :username")
    fun getCurrentUserID(username: String): Flow<User>

    @Query("SELECT * from users ORDER BY userID ASC")
    fun getAllUsers(): Flow<List<User>>

}