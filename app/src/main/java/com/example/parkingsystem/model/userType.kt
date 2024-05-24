package com.example.parkingsystem.model


import com.example.parkingsystem.data.business.businessUser
import com.example.parkingsystem.data.user.User

sealed class UserType {
    data class RegularUser(val user: User) : UserType()
    data class BusinessUser(val businessUser: businessUser?) : UserType()
}