package com.planify.planifyspring.main.features.profiles.domain.use_cases_impl

import com.planify.planifyspring.core.exceptions.NotFoundAppError
import com.planify.planifyspring.main.exceptions.generics.NotFoundHttpException
import com.planify.planifyspring.main.features.profiles.domain.entiries.Profile
import com.planify.planifyspring.main.features.profiles.domain.schemas.PatchProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.services.ProfilesService
import com.planify.planifyspring.main.features.profiles.domain.use_cases.ProfilesUseCaseGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProfilesUseCaseGroupImpl(
    val profilesService: ProfilesService,
) : ProfilesUseCaseGroup {
    override fun getProfileById(userId: Long): Profile {
        return profilesService.getProfileById(userId) ?: throw NotFoundHttpException("Profile for this user $userId was not found")
    }

    override fun patchProfile(userId: Long, patch: PatchProfileSchema) {
        try {
            return profilesService.patchProfile(userId, patch)
        } catch (_: NotFoundAppError) {  // TODO: Do (select -> modify) to throw NotFoundAppError or keep it like this?
            throw NotFoundHttpException("Profile for this user $userId was not found")
        }
    }

    override fun search(
        input: String,
        pageable: Pageable
    ): Page<Profile> {
        return profilesService.search(input, pageable)
    }
}
