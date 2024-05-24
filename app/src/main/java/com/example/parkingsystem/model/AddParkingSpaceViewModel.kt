package com.example.parkingsystem.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingsystem.data.business.businessUser
import com.example.parkingsystem.data.parkingspace.ParkingspaceRepository
import com.example.parkingsystem.data.user.User
import com.example.parkingsystem.data.user.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AddParkingSpaceViewModel(private val repository: ParkingspaceRepository, private val context: Context) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isParkingspaceValid = MutableStateFlow(false)
    val isParkingspaceValid: StateFlow<Boolean> = _isParkingspaceValid

    private val _longitude = MutableStateFlow("")
    val longitude: StateFlow<String> = _longitude

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _latitude = MutableStateFlow("")
    val latitude: StateFlow<String> = _latitude

    private val _rate = MutableStateFlow(0.0)
    val rate: StateFlow<Double> = _rate

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable

    private val _spaceAvailable = MutableStateFlow(0)
    val spaceAvailable: StateFlow<Int> = _spaceAvailable

    private val _capacity = MutableStateFlow(0)
    val capacity: StateFlow<Int> = _capacity

    fun setName(name: String) {
        _name.value = name
    }
    fun setLongitude(longitude: String) {
        _longitude.value = longitude
    }
    fun setLatitude(latitude: String) {
        _latitude.value = latitude
    }
    fun setRate(rate: Double) {
        _rate.value = rate
    }

    fun setIsAvailable(isAvailable: Boolean) {
        _isAvailable.value = isAvailable
    }

    fun setSpaceAvailable(spaceAvailable: Int) {
        _spaceAvailable.value = spaceAvailable
    }

    fun setCapacity(capacity: Int) {
        _capacity.value = capacity
    }


}
