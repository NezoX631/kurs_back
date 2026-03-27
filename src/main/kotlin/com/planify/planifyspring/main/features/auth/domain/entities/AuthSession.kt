package com.planify.planifyspring.main.features.auth.domain.entities

import java.time.Instant

data class AuthSession(
    val uuid: String,
    val userId: Long,
    val userAgent: String,
    val name: String,
    val clientName: String,
    val accessTokenUuid: String,
    val refreshTokenUuid: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val expiresAt: Instant,
    val isActive: Boolean = true
)