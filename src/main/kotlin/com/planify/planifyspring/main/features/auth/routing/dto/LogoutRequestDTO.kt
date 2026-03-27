package com.planify.planifyspring.main.features.auth.routing.dto

data class LogoutRequestDTO(
    val accessToken: String,
    val refreshToken: String
)