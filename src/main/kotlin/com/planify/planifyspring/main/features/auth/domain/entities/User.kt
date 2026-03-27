package com.planify.planifyspring.main.features.auth.domain.entities

import java.io.Serializable

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val passwordHash: String
) : Serializable
