package com.planify.planifyspring.main.features.auth.routing.dto.get_auth_context

import com.planify.planifyspring.main.features.auth.routing.dto.AuthContextDTO

data class GetAuthContextResponseDTO(
    val context: AuthContextDTO
)
