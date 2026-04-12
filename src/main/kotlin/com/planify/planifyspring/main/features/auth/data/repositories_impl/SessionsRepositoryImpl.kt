package com.planify.planifyspring.main.features.auth.data.repositories_impl

import com.planify.planifyspring.main.features.auth.domain.entities.AuthSession
import com.planify.planifyspring.main.features.auth.domain.repositories.SessionsRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.*

@Repository
class SessionsRepositoryImpl(
    // private val redisHelper: RedisHelper
) : SessionsRepository {

    // In-memory session storage (since Redis is disabled)
    private val sessions = ConcurrentHashMap<String, AuthSession>()

    override fun createSession(
        userId: Long,
        userAgent: String,
        sessionName: String,
        clientName: String,
        accessTokenUuid: String,
        refreshTokenUuid: String
    ): AuthSession {
        val session = AuthSession(
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
        sessions[session.uuid] = session
        return session
    }

    override fun getSession(userId: Long, sessionUuid: String): AuthSession? {
        return sessions[sessionUuid]
    }

    override fun getUserSessions(userId: Long): List<AuthSession> {
        return sessions.values.filter { it.userId == userId }
    }

    override fun getActiveUserSessions(userId: Long): List<AuthSession> {
        return sessions.values.filter { it.userId == userId && it.isActive }
    }

    override fun revokeSession(userId: Long, sessionUuid: String, soft: Boolean) {
        if (soft) {
            sessions[sessionUuid]?.let {
                sessions[sessionUuid] = it.copy(isActive = false, updatedAt = Instant.now())
            }
        } else {
            sessions.remove(sessionUuid)
        }
    }

    override fun <T : Any> updateSession(userId: Long, sessionUuid: String, set: Pair<String, T>) {
        sessions[sessionUuid]?.let { current ->
            val updated = when (set.first) {
                "accessTokenUuid" -> current.copy(accessTokenUuid = set.second as String, updatedAt = Instant.now())
                "refreshTokenUuid" -> current.copy(refreshTokenUuid = set.second as String, updatedAt = Instant.now())
                "isActive" -> current.copy(isActive = set.second as Boolean, updatedAt = Instant.now())
                else -> current
            }
            sessions[sessionUuid] = updated
        }
    }

    override fun updateSession(updatedSession: AuthSession) {
        sessions[updatedSession.uuid] = updatedSession
    }
}