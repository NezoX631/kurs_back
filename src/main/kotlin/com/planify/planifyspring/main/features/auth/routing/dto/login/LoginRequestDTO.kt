package com.planify.planifyspring.main.features.auth.routing.dto

data class LoginRequestDTO(
    val email: String,  // Добавьте это поле
    val password: String,
    val clientName: String
)