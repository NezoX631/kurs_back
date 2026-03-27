package com.planify.planifyspring.main.features.auth.routing.dto.refresh

data class RefreshResponseDTO(
    val accessToken: String,
    val refreshToken: String
)
