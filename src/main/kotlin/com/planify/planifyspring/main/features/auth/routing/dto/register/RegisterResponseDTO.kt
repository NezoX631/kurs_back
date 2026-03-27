package com.planify.planifyspring.main.features.auth.routing.dto.register

import com.planify.planifyspring.main.features.auth.routing.dto.*

data class RegisterResponseDTO(
    val user: UserDTO,
    val session: AuthSessionPrivateDTO,
    val tokens: TokensDTO,
    val accessInfo: AccessInfoDTO
) {
    data class UserDTO(
        val id: Long,
        val username: String,
        val email: String
    )
}