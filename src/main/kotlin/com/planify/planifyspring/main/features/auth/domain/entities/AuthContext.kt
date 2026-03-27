package com.planify.planifyspring.main.features.auth.domain.entities

data class AuthContext(
    val user: User,
    val session: AuthSession,
    val tokens: AuthTokenPair,  // Убедитесь, что поле называется 'tokens'
    val accessInfo: AccessInfo
)