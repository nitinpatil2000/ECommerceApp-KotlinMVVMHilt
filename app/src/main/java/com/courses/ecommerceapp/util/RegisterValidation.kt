package com.courses.ecommerceapp.util

sealed class RegisterValidation {
    data object Success:RegisterValidation()
    data class Failed(val message:String):RegisterValidation()
}

data class RegistrationFields(
    val email:RegisterValidation,
    val password:RegisterValidation
)