package com.planify.planifyspring.main.features.auth.domain.entities

import java.time.Instant


data class AuthTokenPayload(
    val uuid: String,
    val type: AuthTokenType,
    val expiresAt: Instant,
    val userId: Long,
    val sessionUuid: String
)
