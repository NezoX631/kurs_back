package com.planify.planifyspring.main.features.profiles.routing.dto

import com.planify.planifyspring.main.features.profiles.domain.entiries.Profile

data class ProfileDTO(
    var userId: Long,
    val firstName: String,
    val lastName: String,
    val position: String?,
    val department: String?,
    val profileImageUrl: String
) {
    companion object {
        fun fromEntity(entity: Profile) = ProfileDTO(
            userId = entity.userId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            position = entity.position,
            department = entity.department,
            profileImageUrl = entity.profileImageUrl
        )
    }
}
