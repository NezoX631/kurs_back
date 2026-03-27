package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenPair

data class TokensDTO(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun fromDomain(tokens: AuthTokenPair): TokensDTO {
            return TokensDTO(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken
            )
        }
    }
}