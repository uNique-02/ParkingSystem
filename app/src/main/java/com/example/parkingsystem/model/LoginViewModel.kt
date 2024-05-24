package com.example.parkingsystem.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingsystem.data.business.BusinessRepository
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UsersRepository, private val businessRepository: BusinessRepository, val context: Context ) : ViewModel() {

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

    private val _poiAddress = MutableStateFlow("")
    val poiAddress: StateFlow<String> = _poiAddress.asStateFlow()

    private val _distance = MutableStateFlow("")
    val distance: StateFlow<String> = _distance.asStateFlow()

    var showBottomSheet by mutableStateOf(false)
        private set

    fun toggleBottomSheet() {
        showBottomSheet = !showBottomSheet
    }

    fun setPoiAddress(newPoiAddress: String) {
        _poiAddress.value = newPoiAddress
    }

    fun setDistance(newDistance: String) {
        _distance.value = newDistance
    }

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
            val isValid = repository.isUserValid(username, password) || businessRepository.isUserValid(username, password)

            Log.e("LoginViewModel", "isValid User: " + repository.isUserValid(username, password))
            Log.e("LoginViewModel", "isValid Business: " + businessRepository.isUserValid(username, password))

            if(isValid){
                SharedPreferencesUtils.setIsLoggedIn(context, isValid)
                val currentUser = repository.getUserStream(username).firstOrNull()

                if(currentUser != null){
                    Log.e("ProfileViewModel", "currentUser not a businessuser: " + currentUser)
                    SharedPreferencesUtils.setCurrentUser(context, currentUser)
                }else{
                    val currentBusinessUser = businessRepository.getUserStream(username).firstOrNull()
                    Log.e("ProfileViewModel", "currentUser is a businessuser: " + currentBusinessUser)
                    if(currentBusinessUser != null){
                        SharedPreferencesUtils.setCurrentUser(context, null, currentBusinessUser)
                    }
                }
            }
            _isLoggedIn.value = isValid
            onResult(isValid)
        }
    }
}
