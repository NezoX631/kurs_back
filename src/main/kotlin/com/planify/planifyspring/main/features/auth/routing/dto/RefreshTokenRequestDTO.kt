package com.planify.planifyspring.main.features.auth.routing.dto

data class RefreshTokenRequestDTO(
    val refreshToken: String,
    val clientName: String
)