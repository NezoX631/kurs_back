package com.planify.planifyspring.main.features.auth.domain.services_impl

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
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
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(this::class.java)

    // Хранилище FCM токенов в памяти (в продакшене использовать БД)
    private val fcmTokens = mutableMapOf<Long, String>()

    private fun generateTokenUuid(): String {
        return tokensRepository.generateTokenUuid()
    }

    private fun saveSession(session: AuthSession) {
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
        )
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
        // Direct repository call - sessions are stored in-memory by SessionsRepositoryImpl
        val session = sessionsRepository.getSession(
            userId = userId,
            sessionUuid = sessionUuid
        ) ?: throw NotFoundAppError("Session not found")
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

    override fun saveFcmToken(userId: Long, fcmToken: String) {
        fcmTokens[userId] = fcmToken
        logger.info("FCM token saved for user $userId")
    }

    override fun getFcmToken(userId: Long): String? {
        return fcmTokens[userId]
    }

    override fun sendPushNotification(fcmToken: String, title: String, body: String, type: String, data: Map<String, String>) {
        try {
            val message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .putData("type", type)
                .apply { data.forEach { (key, value) -> putData(key, value) } }
                .build()

            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("Push notification sent: $response")
        } catch (e: Exception) {
            logger.error("Failed to send push notification: ${e.message}", e)
        }
    }
}
