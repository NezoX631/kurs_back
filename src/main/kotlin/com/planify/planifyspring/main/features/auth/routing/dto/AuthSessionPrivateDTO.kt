package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.AuthSession
import java.time.Instant

data class AuthSessionPrivateDTO(
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
    val isActive: Boolean
) {
    companion object {
        fun fromDomain(session: AuthSession): AuthSessionPrivateDTO {
            return AuthSessionPrivateDTO(
                uuid = session.uuid,
                userId = session.userId,
                userAgent = session.userAgent,
                name = session.name,
                clientName = session.clientName,
                accessTokenUuid = session.accessTokenUuid,
                refreshTokenUuid = session.refreshTokenUuid,
                createdAt = session.createdAt,
                updatedAt = session.updatedAt,
                expiresAt = session.expiresAt,
                isActive = session.isActive
            )
        }
    }
}