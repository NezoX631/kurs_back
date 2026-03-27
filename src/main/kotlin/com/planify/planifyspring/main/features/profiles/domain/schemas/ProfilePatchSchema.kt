package com.planify.planifyspring.main.features.profiles.domain.schemas

data class ProfilePatchSchema(
    var firstName: String? = null,
    var lastName: String? = null,
    var position: String? = null,
    var department: String? = null,
    var profileImageUrl: String? = null,
)
