package com.planify.planifyspring.main.features.profiles.routing.dto.update

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class UpdateProfileRequestDTO(
    val firstName: String,
    val lastName: String,
    val position: String,
    val department: String,

    @field:NotBlank
    @field:URL(
        protocol = "https",
        message = "Profile URL must be a valid HTTP or HTTPS URL"
    )
    val profileImageUrl: String
)
