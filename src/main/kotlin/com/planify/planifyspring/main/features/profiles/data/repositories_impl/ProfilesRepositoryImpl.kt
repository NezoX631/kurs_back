package com.planify.planifyspring.main.features.profiles.data.repositories_impl

import com.planify.planifyspring.main.features.profiles.data.jpa.ProfilesJpaRepository
import com.planify.planifyspring.main.features.profiles.data.models.ProfileModel
import com.planify.planifyspring.main.features.profiles.data.specifications.ProfileSearchSpecification
import com.planify.planifyspring.main.features.profiles.domain.entiries.Profile
import com.planify.planifyspring.main.features.profiles.domain.repositories.ProfilesRepository
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.schemas.PatchProfileSchema
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ProfilesRepositoryImpl(
    private val profilesJpaRepository: ProfilesJpaRepository
) : ProfilesRepository {

    override fun getProfileById(userId: Long): Profile? {
        return profilesJpaRepository.findByUserId(userId)?.toEntity()
    }

    override fun patchProfile(
        userId: Long,
        patch: PatchProfileSchema
    ) {
        // Исправлено: parchProfile -> patchProfile
        profilesJpaRepository.parchProfile(
            userId = userId,
            firstName = patch.firstName,
            lastName = patch.lastName,
            position = patch.position,
            department = patch.department,
            profileImageUrl = patch.profileImageUrl,
        )
    }

    override fun search(
        input: String,
        pageable: Pageable
    ): Page<Profile> {
        return profilesJpaRepository.searchProfiles(input.trim(), pageable)
            .map { it.toEntity() }
    }

    override fun createProfile(
        userId: Long,
        schema: CreateProfileSchema
    ): Profile {
        val profile = ProfileModel(
            userId = userId,
            firstName = schema.firstName ?: "",
            lastName = schema.lastName ?: "",
            position = schema.position ?: "",
            department = schema.department,
            profileImageUrl = schema.profileImageUrl ?: "https://dummyimage.com/512x512/ffae00/000000.png"
        )

        profilesJpaRepository.save(profile)

        return profile.toEntity()
    }
}