package com.planify.planifyspring.main.features.auth.domain.entities

import java.io.Serializable
import java.time.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val passwordHash: String,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
) : Serializable
