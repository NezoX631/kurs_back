package com.planify.planifyspring.main.features.auth.domain.use_cases

import com.planify.planifyspring.main.features.auth.domain.entities.*
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AuthUseCaseGroup {
    fun authenticate(accessToken: String): AuthContext

    fun getSession(userId: Long, sessionUuid: String): AuthSession
    fun getUserSessions(userId: Long): List<AuthSession>
    fun getActiveUserSessions(userId: Long): List<AuthSession>
    fun revokeSession(userId: Long, sessionUuid: String)

    fun refresh(
        refreshToken: String,
        currentUserAgent: String
    ): AuthTokenPair

    fun login(
        email: String,
        passwordRaw: String,
        userAgent: String,
        clientName: String,
        sessionName: String? = null
    ): Pair<AuthContext, AuthTokenPair>

    fun register(
        username: String,
        email: String,
        passwordRaw: String,
        userAgent: String,
        clientName: String,
        createProfileSchema: CreateProfileSchema,
        sessionName: String? = null
    ): Pair<AuthContext, AuthTokenPair>

    fun createUser(
        username: String,
        email: String,
        passwordRaw: String,
        createProfileSchema: CreateProfileSchema
    ): User

    fun getUserById(id: Long): User
    fun getUserByIdWithAccessInfo(id: Long): Pair<User, AccessInfo>
    fun getAllUsersPaginated(pageable: Pageable): Page<User>

    fun getUserByCredentials(email: String, passwordRaw: String): User
    fun getUserByCredentialsWithAccessInfo(email: String, passwordRaw: String): Pair<User, AccessInfo>
}
