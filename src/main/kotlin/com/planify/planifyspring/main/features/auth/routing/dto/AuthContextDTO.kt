package com.planify.planifyspring.main.features.auth.routing.dto

data class AuthContextDTO(
    val user: UserPrivateDTO,
    val session: AuthSessionPrivateDTO,
    val tokens: TokensDTO,
    val accessInfo: AccessInfoDTO
)