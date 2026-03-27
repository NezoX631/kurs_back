package com.planify.planifyspring.main.common.utils

import io.jsonwebtoken.security.Keys
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

object SecurityHelper {
    val secretString = System.getenv("JWT_SECRET")!!
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
