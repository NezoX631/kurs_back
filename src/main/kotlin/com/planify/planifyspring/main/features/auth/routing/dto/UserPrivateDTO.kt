package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.User

data class UserPrivateDTO(
    val id: Long,
    val username: String,
    val email: String
) {
    companion object {
        fun fromDomain(user: User): UserPrivateDTO =
            UserPrivateDTO(
                id = user.id,
                username = user.username,
                email = user.email
            )

        // Алиас для совместимости с UsersFeatureController
        fun fromEntity(user: User): UserPrivateDTO = fromDomain(user)
    }
}