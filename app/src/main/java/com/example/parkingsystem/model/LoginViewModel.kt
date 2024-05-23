package com.example.parkingsystem.viewmodel

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UsersRepository, private val context: Context ) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(SharedPreferencesUtils.getIsLoggedIn(context))
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn.asStateFlow()

    // Scaffold state to manage Snackbar
    val scaffoldState = SnackbarHostState()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isUsernameValid = MutableStateFlow(false)
    val isUsernameValid: StateFlow<Boolean> = _isUsernameValid

    private val _isPasswordValid = MutableStateFlow(false)
    val isPasswordValid: StateFlow<Boolean> = _isPasswordValid

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
        _isUsernameValid.value = newUsername.isNotEmpty()
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = newPassword.isNotEmpty()
    }

    fun login() {
        viewModelScope.launch {
            // Handle login logic here

        }
    }

    fun logout() {
        SharedPreferencesUtils.setIsLoggedIn(context, false)
        _isLoggedIn.value = false
    }

    fun checkUserCredentials(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isValid = repository.isUserValid(username, password)
            if(isValid){
                SharedPreferencesUtils.setIsLoggedIn(context, isValid)
                val currentUser = repository.getUserStream(username).firstOrNull()

                // Set the current user in SharedPreferences
                SharedPreferencesUtils.setCurrentUser(context, currentUser)
            }
            _isLoggedIn.value = isValid
            onResult(isValid)
        }
    }
}
