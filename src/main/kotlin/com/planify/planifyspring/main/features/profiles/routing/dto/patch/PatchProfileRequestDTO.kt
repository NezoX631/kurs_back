package com.planify.planifyspring.main.features.profiles.routing.dto.patch

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class PatchProfileRequestDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val position: String? = null,
    val department: String? = null,

    @field:NotBlank
    @field:URL(
        protocol = "https",
        message = "Profile URL must be a valid HTTP or HTTPS URL"
    )
    val profileImageUrl: String? = null
)
