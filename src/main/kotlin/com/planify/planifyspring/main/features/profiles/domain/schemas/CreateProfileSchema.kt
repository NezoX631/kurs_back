package com.planify.planifyspring.main.features.profiles.domain.schemas

data class CreateProfileSchema(
    val firstName: String?,
    val lastName: String?,
    val position: String?,
    val department: String?,
    val profileImageUrl: String?
)