package com.planify.planifyspring.main.features.auth.routing.dto.get_user_sessions

import com.planify.planifyspring.main.features.auth.routing.dto.AuthSessionPrivateDTO

data class GetSessionsResponseDTO(
    val sessions: List<AuthSessionPrivateDTO>
)
