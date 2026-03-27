package com.planify.planifyspring.main.features.profiles.routing.dto.search

import com.planify.planifyspring.main.features.profiles.routing.dto.ProfileDTO
import org.springframework.data.domain.Page

data class SearchProfilesResponseDTO(
    val result: Page<ProfileDTO>
)
