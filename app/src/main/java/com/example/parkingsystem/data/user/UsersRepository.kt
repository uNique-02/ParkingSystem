package com.example.parkingsystem.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface UsersRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllUsersStream(): Flow<List<User>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getUserStream(username: String): Flow<User?>

    fun getCurrentUser(userID: Int): Flow<User?>

    fun getCurrentUserID(username: String): Flow<User?>

    /**
     * Insert item in the data source
     */
    suspend fun insertUser(user: User)

    /**
     * Delete item from the data source
     */
    suspend fun deleteUser(user: User)

    /**
     * Update item in the data source
     */
    suspend fun updateUser(user: User)

    suspend fun isUserValid(username: String, password: String): Boolean
}

class OfflineUsersRepository(private val userDao: UserDao) : UsersRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUserStream(username: String): Flow<User?> = userDao.getUser(username)
    override fun getCurrentUser(userID: Int): Flow<User?> = userDao.getCurrentUser(userID)
    override fun getCurrentUserID(username: String): Flow<User?> = userDao.getCurrentUserID(username)

    override suspend fun insertUser(user: User) = userDao.insert(user)

    override suspend fun deleteUser(user: User) = userDao.delete(user)

    override suspend fun updateUser(user: User) = userDao.update(user)

    override suspend fun isUserValid(username: String, password: String): Boolean {
        val user = userDao.getUser(username).firstOrNull()
        return user?.password == password
    }
}


