package com.planify.planifyspring.main.features.auth.domain.repositories

import com.planify.planifyspring.main.features.auth.domain.entities.AuthSession

interface SessionsRepository {
    fun createSession(
        userId: Long,
        userAgent: String,
        sessionName: String,
        clientName: String,
        accessTokenUuid: String,
        refreshTokenUuid: String
    ): AuthSession

    fun getSession(userId: Long, sessionUuid: String): AuthSession?

    fun getUserSessions(userId: Long): List<AuthSession>
    fun getActiveUserSessions(userId: Long): List<AuthSession>

    fun revokeSession(userId: Long, sessionUuid: String, soft: Boolean = true)

    fun <T: Any> updateSession(userId: Long, sessionUuid: String, set: Pair<String, T>)
    fun updateSession(updatedSession: AuthSession)
}
