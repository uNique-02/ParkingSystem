package com.example.parkingsystem.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingsystem.data.business.BusinessRepository
import com.example.parkingsystem.data.business.businessUser
import com.example.parkingsystem.data.user.User
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel
    (private val repository: UsersRepository, private val businessRepository: BusinessRepository, context: Context) : ViewModel() {

    private val _isBusinessAccount = MutableStateFlow(false)
    val isBusinessAccount: StateFlow<Boolean> = _isBusinessAccount

    private val _fName = MutableStateFlow("")
    val fName: StateFlow<String> = _fName

    private val _lName = MutableStateFlow("")
    val lName: StateFlow<String> = _lName

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _pNumber = MutableStateFlow("")
    val pNumber: StateFlow<String> = _pNumber

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPhoneNumberValid = MutableStateFlow(false)
    val isPhoneNumberValid: StateFlow<Boolean> = _isPhoneNumberValid

    private val _isfNameValid = MutableStateFlow(false)
    val isfNameValid: StateFlow<Boolean> = _isfNameValid

    private val _islNameValid = MutableStateFlow(false)
    val islNameValid: StateFlow<Boolean> = _islNameValid

    private val _isuNameValid = MutableStateFlow(false)
    val isuNameValid: StateFlow<Boolean> = _isuNameValid

    private val _isPasswordStrong = MutableStateFlow(false)
    val isPasswordStrong: StateFlow<Boolean> = _isPasswordStrong

    fun toggleAccountType() {
        _isBusinessAccount.value = !_isBusinessAccount.value
        Log.e("CustomToggleSwitch", "isOn: " + isBusinessAccount.value)
    }

    fun onfNameChange(newName: String) {
        _fName.value = newName
        _isfNameValid.value = newName.isNotEmpty()
    }

    fun onlNameChange(newName: String) {
        _lName.value = newName
        _islNameValid.value = newName.isNotEmpty()
    }

    fun onAddressChange(newAddress: String) {
        _address.value = newAddress
    }

    fun onPhoneNumberChange(newNumber: String) {
        _pNumber.value = newNumber
        _isPhoneNumberValid.value = isValidPhoneNumber(newNumber)
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
        _isuNameValid.value = newUsername.isNotEmpty()
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordStrong.value = isStrongPassword(newPassword)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberPattern = Regex("^09\\d{9}$")
        return phoneNumberPattern.matches(phoneNumber)
    }

    private fun isStrongPassword(password: String): Boolean {
        val minLength = 8
        if (password.length < minLength) return false
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { it in "!@#$%^&*()-_=+{}[]|:;\"'<>,.?/" }
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    fun register() {
        if(isBusinessAccount.value) {
            registerBusiness()
        } else {
            registerUser()
        }
    }

    fun registerBusiness() {
        Log.e("RegisterViewModel", "Registering business user")
        val newUser = businessUser(
            businessName = _fName.value + " " + _lName.value,
            businessAddress = _address.value,
            businessNumber = _pNumber.value,
            businessUsername = _username.value,
            businessPassword = _password.value
        )
        viewModelScope.launch {
            businessRepository.insertUser(newUser)
        }
    }
    fun registerUser() {
        Log.e("RegisterViewModel", "Registering regular user")
        val newUser = User(
            username = _username.value,
            password = _password.value,
            fName = _fName.value,
            lName = _lName.value,
            address = _address.value,
            pNumber = _pNumber.value
        )
        viewModelScope.launch {
            repository.insertUser(newUser)
        }
    }
}
