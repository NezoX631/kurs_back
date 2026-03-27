package com.planify.planifyspring.main.features.profiles.routing

import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.common.utils.asSuccessApplicationResponse
import com.planify.planifyspring.main.features.auth.domain.entities.AuthContext
import com.planify.planifyspring.main.features.profiles.domain.schemas.PatchProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.use_cases.ProfilesUseCaseGroup
import com.planify.planifyspring.main.features.profiles.routing.dto.ProfileDTO
import com.planify.planifyspring.main.features.profiles.routing.dto.get_profile.GetProfileResponseDTO
import com.planify.planifyspring.main.features.profiles.routing.dto.patch.PatchProfileRequestDTO
import com.planify.planifyspring.main.features.profiles.routing.dto.search.SearchProfilesResponseDTO
import com.planify.planifyspring.main.features.profiles.routing.dto.update.UpdateProfileRequestDTO
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profiles")
class MyProfileController(
    private val profilesUseCaseGroup: ProfilesUseCaseGroup
) {
    @GetMapping("/my")
    fun getProfile(
        @AuthenticationPrincipal authContext: AuthContext
    ): ResponseEntity<ApplicationResponse<GetProfileResponseDTO>> {
        val profile = profilesUseCaseGroup.getProfileById(authContext.user.id)

        return ResponseEntity.ok(
            GetProfileResponseDTO(
                profile = ProfileDTO.fromEntity(profile)
            ).asSuccessApplicationResponse()
        )
    }

    @PatchMapping("/my")
    fun patchProfile(
        @AuthenticationPrincipal authContext: AuthContext,
        @Valid @RequestBody body: PatchProfileRequestDTO
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        profilesUseCaseGroup.patchProfile(authContext.user.id, PatchProfileSchema(
            firstName = body.firstName,
            lastName = body.lastName,
            position = body.position,
            department = body.department,
            profileImageUrl = body.profileImageUrl
        ))

        return ResponseEntity.ok(ApplicationResponse.success())
    }

    @PutMapping("/my")
    fun updateProfile(
        @AuthenticationPrincipal authContext: AuthContext,
        @RequestBody body: UpdateProfileRequestDTO
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        profilesUseCaseGroup.patchProfile(authContext.user.id, PatchProfileSchema(
            firstName = body.firstName,
            lastName = body.lastName,
            position = body.position,
            department = body.department,
            profileImageUrl = body.profileImageUrl
        ))

        return ResponseEntity.ok(ApplicationResponse.success())
    }

    @GetMapping("/search")  // Public endpoint?
    fun search(
        @PageableDefault pageable: Pageable,
        @RequestParam query: String,
    ): ResponseEntity<ApplicationResponse<SearchProfilesResponseDTO>> {
        val result = profilesUseCaseGroup.search(query, pageable)

        return ResponseEntity.ok(
            SearchProfilesResponseDTO(
                result = result.map { ProfileDTO.fromEntity(it) }
            ).asSuccessApplicationResponse()
        )
    }
}
