package com.planify.planifyspring.main.features.auth.domain.services_impl

import com.planify.planifyspring.core.exceptions.NotFoundAppError
import com.planify.planifyspring.main.common.utils.JsonCacheWrapper
import com.planify.planifyspring.main.common.utils.SecurityHelper
import com.planify.planifyspring.main.features.auth.domain.entities.*
import com.planify.planifyspring.main.features.auth.domain.repositories.SessionsRepository
import com.planify.planifyspring.main.features.auth.domain.repositories.TokensRepository
import com.planify.planifyspring.main.features.auth.domain.repositories.UsersRepository
import com.planify.planifyspring.main.features.auth.domain.services.AuthService
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.services.ProfilesService
import org.springframework.cache.CacheManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class AuthServiceImpl(
    private val tokensRepository: TokensRepository,
    private val sessionsRepository: SessionsRepository,
    private val usersRepository: UsersRepository,
    private val cacheManager: CacheManager,
    private val objectMapper: ObjectMapper,
    private val profilesService: ProfilesService
) : AuthService {
    private fun generateTokenUuid(): String {
        return tokensRepository.generateTokenUuid()
    }

    private fun saveSession(session: AuthSession) {
        val cache = cacheManager.getCache("sessions")
        cache?.evict("${session.userId}-${session.uuid}")

        sessionsRepository.updateSession(session)
    }

    override fun decodeJwtToken(token: String): AuthTokenPayload {
        return tokensRepository.decodeJwtToken(token)
    }

    private fun createSession(
        userId: Long,
        userAgent: String,
        sessionName: String,
        clientName: String,
        accessTokenUuid: String,
        refreshTokenUuid: String
    ): AuthSession {
        return sessionsRepository.createSession(
            userId = userId,
            userAgent = userAgent,
            sessionName = sessionName,
            accessTokenUuid = accessTokenUuid,
            refreshTokenUuid = refreshTokenUuid,
            clientName = clientName
        ).also {
            val cache = JsonCacheWrapper(cacheManager.getCache("sessions")!!, objectMapper)
            cache.put("${it.userId}-${it.uuid}", it)
        }
    }

    override fun startSession(
        userId: Long,
        userAgent: String,
        sessionName: String,
        clientName: String
    ): Pair<AuthSession, AuthTokenPair> {

        val newAccessTokenUuid = generateTokenUuid()
        val newRefreshTokenUuid = generateTokenUuid()

        val session = createSession(
            userId = userId,
            userAgent = userAgent,
            sessionName = sessionName,
            accessTokenUuid = newAccessTokenUuid,
            refreshTokenUuid = newRefreshTokenUuid,
            clientName = clientName
        )

        return session to AuthTokenPair(
            accessToken = tokensRepository.createAccessToken(
                userId = userId,
                sessionUuid = session.uuid,
                tokenUuid = newAccessTokenUuid
            ),
            refreshToken = tokensRepository.createRefreshToken(
                userId = userId,
                sessionUuid = session.uuid,
                tokenUuid = newRefreshTokenUuid
            )
        )
    }

    override fun getSession(userId: Long, sessionUuid: String): AuthSession {
        val cache = JsonCacheWrapper(cacheManager.getCache("sessions")!!, objectMapper)
        val cached = cache.getAs<AuthSession>("$userId-$sessionUuid")
        if (cached != null) return cached

        val session = sessionsRepository.getSession(
            userId = userId,
            sessionUuid = sessionUuid
        ) ?: throw NotFoundAppError("Session not found")

        cache.put("$userId-$sessionUuid", session)
        return session
    }

    override fun getUserSessions(userId: Long): List<AuthSession> {
        return sessionsRepository.getUserSessions(userId)
    }

    override fun getActiveUserSessions(userId: Long): List<AuthSession> {
        return sessionsRepository.getActiveUserSessions(userId)
    }

    override fun rotateSessionTokens(session: AuthSession): AuthTokenPair {
        val newAccessTokenPayload = tokensRepository.createAccessTokenPayload(userId = session.userId, sessionUuid = session.uuid)
        val newRefreshTokenPayload = tokensRepository.createRefreshTokenPayload(userId = session.userId, sessionUuid = session.uuid)

        saveSession(
            session.copy(
                accessTokenUuid = newAccessTokenPayload.uuid,
                refreshTokenUuid = newRefreshTokenPayload.uuid,
            )
        )

        return AuthTokenPair(
            accessToken = tokensRepository.createAccessToken(newAccessTokenPayload),
            refreshToken = tokensRepository.createRefreshToken(newRefreshTokenPayload),
        )
    }

    override fun rotateSessionTokens(userId: Long, sessionUuid: String): AuthTokenPair {
        return rotateSessionTokens(session = getSession(userId, sessionUuid))
    }

    override fun revokeSession(userId: Long, sessionUuid: String) {
        val cache = cacheManager.getCache("sessions")!!
        cache.evict("$userId-$sessionUuid")

        return sessionsRepository.revokeSession(userId = userId, sessionUuid = sessionUuid, soft = true)
    }

    override fun createUser(
        username: String,
        email: String,
        passwordRaw: String,
        createProfileSchema: CreateProfileSchema
    ): User {
        val user = usersRepository.create(
            username = username,
            email = email,
            passwordHash = SecurityHelper.hashPassword(passwordRaw)
        ).also {
            val cache = JsonCacheWrapper(cacheManager.getCache("users")!!, objectMapper)
            cache.put(it.id.toString(), it)
        }

        profilesService.createProfile(user.id, createProfileSchema)

        return user
    }

    override fun getUserById(id: Long): User {
        val cache = JsonCacheWrapper(cacheManager.getCache("users")!!, objectMapper)
        val cached = cache.getAs<User>(id.toString())
        if (cached != null) return cached

        val user = usersRepository.getById(id)
        return user ?: throw NotFoundAppError("User was not found")
    }

    override fun getUserByIdWithAccessInfo(id: Long): Pair<User, AccessInfo> {
        val cache = JsonCacheWrapper(cacheManager.getCache("usersWithAccess")!!, objectMapper)
        val cached = cache.getAs<Pair<User, AccessInfo>>(id.toString())
        if (cached != null) return cached

        val result = usersRepository.getByIdWithAccessInfo(id)
        return result ?: throw NotFoundAppError("User was not found")
    }

    override fun getAllUsersPaginated(pageable: Pageable): Page<User> {
        return usersRepository.getAllUsersPaginated(pageable)
    }

    override fun getUserByCredentials(  // TODO: Cache?
        email: String,
        passwordRaw: String
    ): User {
        val user = usersRepository.getByAuthCredentials(email, passwordRaw)
        return user ?: throw NotFoundAppError("User was not found")
    }

    override fun getUserByCredentialsWithAccessInfo(email: String, passwordRaw: String): Pair<User, AccessInfo> {
        return usersRepository.getByAuthCredentialsWithAccessInfo(email, passwordRaw) ?: throw NotFoundAppError("User was not found")
    }
}
