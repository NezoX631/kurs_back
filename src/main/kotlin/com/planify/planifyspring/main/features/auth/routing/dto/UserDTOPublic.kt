package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.User

interface UserPublicDTOI {
    val id: Long
    val username: String
}

data class UserPublicDTO(
    override val id: Long,
    override val username: String,
) : UserPublicDTOI {
    companion object {
        fun fromUserEntity(entity: User): UserPublicDTO = UserPublicDTO(
            id = entity.id,
            username = entity.username
        )
    }
}
