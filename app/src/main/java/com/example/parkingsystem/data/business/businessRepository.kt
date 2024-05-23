package com.example.parkingsystem.data.business

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface BusinessRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllUsersStream(): Flow<List<businessUser>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getUserStream(username: String): Flow<businessUser?>

    fun getCurrentUser(userID: Int): Flow<businessUser?>

    fun getCurrentUserID(username: String): Flow<businessUser?>

    /**
     * Insert item in the data source
     */
    suspend fun insertUser(user: businessUser)

    /**
     * Delete item from the data source
     */
    suspend fun deleteUser(user: businessUser)

    /**
     * Update item in the data source
     */
    suspend fun updateUser(user: businessUser)

    suspend fun isUserValid(username: String, password: String): Boolean
}

class OfflineBusinessRepository(private val businessUserDao: businessUserDao) : BusinessRepository {
    override fun getAllUsersStream(): Flow<List<businessUser>> = businessUserDao.getAllUsers()

    override fun getUserStream(username: String): Flow<businessUser?> = businessUserDao.getUser(username)
    override fun getCurrentUser(userID: Int): Flow<businessUser?> = businessUserDao.getCurrentUser(userID)
    override fun getCurrentUserID(username: String): Flow<businessUser?> = businessUserDao.getCurrentUserID(username)

    override suspend fun insertUser(user: businessUser) = businessUserDao.insert(user)

    override suspend fun deleteUser(user: businessUser) = businessUserDao.delete(user)

    override suspend fun updateUser(user: businessUser) = businessUserDao.update(user)

    override suspend fun isUserValid(username: String, password: String): Boolean {
        val user = businessUserDao.getUser(username).firstOrNull()
        return user?.businessPassword == password
    }
}


