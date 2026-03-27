package com.planify.planifyspring.main.features.auth.routing.dto.get_all_users

import com.planify.planifyspring.main.features.auth.routing.dto.UserPrivateDTO
import org.springframework.data.domain.Page

data class GetAllUsersResponseDTO(
    val users: Page<UserPrivateDTO>  // Admin endpoint -> can read user private
)
