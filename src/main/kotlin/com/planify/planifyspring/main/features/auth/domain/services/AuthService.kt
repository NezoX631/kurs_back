package com.planify.planifyspring.main.features.auth.domain.services

import com.planify.planifyspring.main.features.auth.domain.entities.AccessInfo
import com.planify.planifyspring.main.features.auth.domain.entities.AuthSession
import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenPair
import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenPayload
import com.planify.planifyspring.main.features.auth.domain.entities.User
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AuthService {
    fun decodeJwtToken(token: String): AuthTokenPayload

    fun startSession(userId: Long, userAgent: String, sessionName: String, clientName: String): Pair<AuthSession, AuthTokenPair>

    fun getSession(userId: Long, sessionUuid: String): AuthSession

    fun getUserSessions(userId: Long): List<AuthSession>
    fun getActiveUserSessions(userId: Long): List<AuthSession>

    fun rotateSessionTokens(session: AuthSession) : AuthTokenPair
    fun rotateSessionTokens(userId: Long, sessionUuid: String): AuthTokenPair

    fun revokeSession(userId: Long, sessionUuid: String)

    fun createUser(username: String, email: String, passwordRaw: String, createProfileSchema: CreateProfileSchema): User

    fun getUserById(id: Long): User
    fun getUserByIdWithAccessInfo(id: Long): Pair<User, AccessInfo>
    fun getAllUsersPaginated(pageable: Pageable): Page<User>

    fun getUserByCredentials(email: String, passwordRaw: String): User
    fun getUserByCredentialsWithAccessInfo(email: String, passwordRaw: String): Pair<User, AccessInfo>
}
