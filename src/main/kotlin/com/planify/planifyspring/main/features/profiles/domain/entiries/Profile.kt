package com.planify.planifyspring.main.features.profiles.domain.entiries

data class Profile(
    val userId : Long,
    val firstName: String,
    val lastName: String,
    val position: String?,
    val department: String?,
    val profileImageUrl: String
)
