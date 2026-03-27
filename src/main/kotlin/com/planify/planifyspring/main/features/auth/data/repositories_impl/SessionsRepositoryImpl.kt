package com.planify.planifyspring.main.features.auth.data.repositories_impl

import com.planify.planifyspring.main.features.auth.domain.entities.AuthSession
import com.planify.planifyspring.main.features.auth.domain.repositories.SessionsRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class SessionsRepositoryImpl(
    // private val redisHelper: RedisHelper
) : SessionsRepository {

    override fun createSession(
        userId: Long,
        userAgent: String,
        sessionName: String,
        clientName: String,
        accessTokenUuid: String,
        refreshTokenUuid: String
    ): AuthSession {
        return AuthSession(
            uuid = UUID.randomUUID().toString(),
            userId = userId,
            userAgent = userAgent,
            name = sessionName,
            clientName = clientName,
            accessTokenUuid = accessTokenUuid,
            refreshTokenUuid = refreshTokenUuid,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            expiresAt = Instant.now().plusSeconds(24 * 60 * 60),
            isActive = true
        )
    }

    override fun getSession(userId: Long, sessionUuid: String): AuthSession? {
        println("getSession called for userId: $userId, sessionUuid: $sessionUuid (returning null - Redis disabled)")
        return null
    }

    override fun getUserSessions(userId: Long): List<AuthSession> {
        println("getUserSessions called for userId: $userId (returning empty list - Redis disabled)")
        return emptyList()
    }

    override fun getActiveUserSessions(userId: Long): List<AuthSession> {
        println("getActiveUserSessions called for userId: $userId (returning empty list - Redis disabled)")
        return emptyList()
    }

    override fun revokeSession(userId: Long, sessionUuid: String, soft: Boolean) {
        println("revokeSession called for userId: $userId, sessionUuid: $sessionUuid, soft: $soft (ignored - Redis disabled)")
    }

    override fun <T : Any> updateSession(userId: Long, sessionUuid: String, set: Pair<String, T>) {
        println("updateSession called for userId: $userId, sessionUuid: $sessionUuid, set: $set (ignored - Redis disabled)")
    }

    override fun updateSession(updatedSession: AuthSession) {
        println("updateSession called for updatedSession: ${updatedSession.uuid} (ignored - Redis disabled)")
    }
}