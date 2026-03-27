package com.planify.planifyspring.main.features.auth.domain.repositories

import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenPayload

interface TokensRepository {
    fun generateTokenUuid(): String

    fun decodeJwtToken(token: String): AuthTokenPayload

    fun createAccessTokenPayload(userId: Long, sessionUuid: String, tokenUuid: String? = null): AuthTokenPayload
    fun createAccessToken(userId: Long, sessionUuid: String, tokenUuid: String? = null): String
    fun createAccessToken(payload: AuthTokenPayload): String

    fun createRefreshTokenPayload(userId: Long, sessionUuid: String, tokenUuid: String? = null): AuthTokenPayload
    fun createRefreshToken(userId: Long, sessionUuid: String, tokenUuid: String? = null): String
    fun createRefreshToken(payload: AuthTokenPayload): String
}
