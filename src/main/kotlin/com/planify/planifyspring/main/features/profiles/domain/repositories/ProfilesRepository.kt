package com.planify.planifyspring.main.features.profiles.domain.repositories

import com.planify.planifyspring.main.features.profiles.domain.entiries.Profile
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.schemas.PatchProfileSchema
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProfilesRepository {
    fun getProfileById(userId: Long): Profile?
    fun patchProfile(userId: Long, patch: PatchProfileSchema)

    fun search(input: String, pageable: Pageable): Page<Profile>

    fun createProfile(userId: Long, schema: CreateProfileSchema): Profile
}
