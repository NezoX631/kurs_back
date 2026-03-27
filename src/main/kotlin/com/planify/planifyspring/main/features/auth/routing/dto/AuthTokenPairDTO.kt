package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenPair

data class AuthTokenPairDTO(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun fromEntity(entity: AuthTokenPair): AuthTokenPairDTO {
            return AuthTokenPairDTO(
                accessToken = entity.accessToken,
                refreshToken = entity.refreshToken
            )
        }
    }
}
