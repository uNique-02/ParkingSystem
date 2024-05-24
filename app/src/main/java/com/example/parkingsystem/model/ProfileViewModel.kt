package com.example.parkingsystem.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingsystem.data.business.businessUser
import com.example.parkingsystem.data.user.User
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UsersRepository, private val context: Context) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserType?>(null)
    val currentUser: StateFlow<UserType?> get() = _currentUser.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> get() = _name.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> get() = _address.asStateFlow()

    private val _pNumber = MutableStateFlow("")
    val pNumber: StateFlow<String> get() = _pNumber.asStateFlow()

    init {
        viewModelScope.launch {
            _currentUser.value = getUserFromPreferences()
            Log.e("ProfileViewModel", "init: " + _currentUser.value.toString() )
            updateProfileFields(_currentUser.value)
        }
    }

    private fun updateProfileFields(userType: UserType?) {
        when (userType) {
            is UserType.RegularUser -> {
                _name.value = "${userType.user.fName} ${userType.user.lName}"
                _address.value = userType.user.address
                _pNumber.value = userType.user.pNumber
            }
            is UserType.BusinessUser -> {
                Log.e("ProfileViewModel", "updateProfileFields: " + userType.businessUser.businessName)
                _name.value = userType.businessUser.businessName
                _address.value = userType.businessUser.businessAddress
                _pNumber.value = userType.businessUser.businessNumber
            }
            else -> {
                _name.value = ""
                _address.value = ""
                _pNumber.value = ""
            }
        }
    }

    private fun getUserFromPreferences(): UserType? {
        val user: User? = SharedPreferencesUtils.getCurrentUser(context)
        Log.e("ProfileViewModel", "getUserFromPreferences: " + user.toString())
        val businessUser: businessUser? = SharedPreferencesUtils.getCurrentBusinessUser(context)
        Log.e("ProfileViewModel", "getUserFromPreferences: " + businessUser.toString())
        return when {
            user?.username != null -> {
                Log.d("ProfileViewModel", "Loaded regular user from preferences: ${user?.username}")
                UserType.RegularUser(user!!)
            }
            businessUser != null -> {
                Log.d("ProfileViewModel", "Loaded business user from preferences: ${businessUser.businessName}")
                UserType.BusinessUser(businessUser)
            }
            else -> {
                Log.d("ProfileViewModel", "No user found in preferences")
                null
            }
        }
    }

    fun setName(newName: String) {
        Log.d("ProfileViewModel", "Setting name to $newName")
        _name.value = newName
    }

    fun setAddress(newAddress: String) {
        Log.d("ProfileViewModel", "Setting address to $newAddress")
        _address.value = newAddress
    }

    fun setpNumber(newpNumber: String) {
        Log.d("ProfileViewModel", "Setting phone number to $newpNumber")
        _pNumber.value = newpNumber
    }

    fun isRegularUser(username: String): Boolean {
        var isRegularUser = false
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Checking if $username is a regular user")
            val user = repository.getUserStream(username).firstOrNull()
            isRegularUser = user != null
            if (!isRegularUser) {
                Log.d("ProfileViewModel", "$username is not a regular user, checking business user")
                val businessUser = SharedPreferencesUtils.getCurrentBusinessUser(context)
                if (businessUser != null) {
                    Log.d("ProfileViewModel", "Loaded business user from preferences: ${businessUser.businessName}")
                    _currentUser.value = UserType.BusinessUser(businessUser)
                }
            } else {
                Log.d("ProfileViewModel", "$username is a regular user")
                _currentUser.value = UserType.RegularUser(user!!)
            }
            updateProfileFields(_currentUser.value)
        }
        return isRegularUser
    }
}
