package com.planify.planifyspring.main.common.utils

import io.jsonwebtoken.security.Keys
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

object SecurityHelper {
    // Fallback ключ для разработки. В продакшене используйте JWT_SECRET из env!
    private const val DEFAULT_SECRET = "bXlzdXBlcnNlY3JldGtleWZvcnRva2Vuc2lnbmluZzE="
    val secretString: String = System.getenv("JWT_SECRET") ?: DEFAULT_SECRET
    val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString))
    val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(12)

    fun calculateAccessTokenExpiresAt(): Instant {
        return Instant.now().plus(1, ChronoUnit.HOURS)
    }

    fun calculateRefreshTokenExpiresAt(): Instant {
        return Instant.now().plus(12, ChronoUnit.HOURS)
    }

    fun calculateSessionExpiresAt(): Instant {
        return Instant.now().plus(12, ChronoUnit.HOURS)
    }

    fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)!!
    }

    fun isPasswordsMatch(password: String, hashedPassword: String): Boolean {
        return passwordEncoder.matches(password, hashedPassword)
    }
}
