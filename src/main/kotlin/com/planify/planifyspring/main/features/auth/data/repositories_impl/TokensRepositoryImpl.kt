package com.planify.planifyspring.main.features.auth.data.repositories_impl

import com.planify.planifyspring.main.common.utils.SecurityHelper
import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenPayload
import com.planify.planifyspring.main.features.auth.domain.entities.AuthTokenType
import com.planify.planifyspring.main.features.auth.domain.repositories.TokensRepository
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class TokensRepositoryImpl : TokensRepository {
    private fun createJwtToken(payload: AuthTokenPayload): String {
        return Jwts.builder()
            .setSubject(payload.userId.toString())
            .setId(payload.uuid)
            .setIssuedAt(Date())
            .setExpiration(Date.from(payload.expiresAt))
            .claim("type", payload.type.code)
            .claim("userId", payload.userId)
            .claim("sessionUuid", payload.sessionUuid)
            .signWith(SecurityHelper.secretKey)
            .compact()
    }

    override fun generateTokenUuid(): String {
        return UUID.randomUUID().toString()
    }

    override fun decodeJwtToken(token: String): AuthTokenPayload {
        val claims = Jwts.parserBuilder()
            .setSigningKey(SecurityHelper.secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        val uuid: String = claims.id
        val expiresAt: Instant = claims.expiration.toInstant()

        val type: AuthTokenType = AuthTokenType.fromCode(claims["type"] as Int)!!
        val sessionUuid: String = claims["sessionUuid"] as String
        val userId = (claims["userId"] as Number).toLong()

        return AuthTokenPayload(
            uuid = uuid,
            type = type,
            expiresAt = expiresAt,
            userId = userId,
            sessionUuid = sessionUuid
        )
    }

    override fun createAccessTokenPayload(
        userId: Long,
        sessionUuid: String,
        tokenUuid: String?
    ): AuthTokenPayload {
        return AuthTokenPayload(
            uuid = tokenUuid ?: generateTokenUuid(),
            type = AuthTokenType.ACCESS,
            expiresAt = SecurityHelper.calculateAccessTokenExpiresAt(),
            userId = userId,
            sessionUuid = sessionUuid,
        )
    }

    override fun createAccessToken(
        userId: Long,
        sessionUuid: String,
        tokenUuid: String?
    ): String {
        val payload = createAccessTokenPayload(userId, sessionUuid, tokenUuid)
        return createJwtToken(payload)
    }

    override fun createAccessToken(payload: AuthTokenPayload): String {
        return createRefreshToken(payload)
    }

    override fun createRefreshTokenPayload(
        userId: Long,
        sessionUuid: String,
        tokenUuid: String?
    ): AuthTokenPayload {
        return AuthTokenPayload(
            uuid = tokenUuid ?: generateTokenUuid(),
            type = AuthTokenType.REFRESH,
            expiresAt = SecurityHelper.calculateRefreshTokenExpiresAt(),
            userId = userId,
            sessionUuid = sessionUuid
        )
    }

    override fun createRefreshToken(
        userId: Long,
        sessionUuid: String,
        tokenUuid: String?
    ): String {
        val payload = createRefreshTokenPayload(userId, sessionUuid, tokenUuid)
        return createJwtToken(payload)
    }

    override fun createRefreshToken(payload: AuthTokenPayload): String {
        return createJwtToken(payload)
    }
}
