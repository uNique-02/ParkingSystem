package com.example.parkingsystem.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.parkingsystem.data.user.User
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel (private val repository: UsersRepository, private val context: Context): ViewModel(){

    private val _currentUser = MutableStateFlow<User?>(SharedPreferencesUtils.getCurrentUser(context))
    val currentUser: StateFlow<User?> get() = _currentUser.asStateFlow()


    private val _name = MutableStateFlow(currentUser.value?.fName + " " + currentUser.value?.lName ?: "")
    val name: StateFlow<String> = _name

    private val _address = MutableStateFlow(currentUser.value?.address ?: "")
    val address: StateFlow<String> = _address

    private val _pNumber = MutableStateFlow(currentUser.value?.pNumber ?: "")
    val pNumber: StateFlow<String> = _pNumber

    fun setName(newName: String) {
        _name.value = newName
    }

    fun setAddress(newAddress: String) {
        _address.value = newAddress
    }

    fun setpNumber(newpNumber: String) {
        _pNumber.value = newpNumber
    }
}