package com.planify.planifyspring.main.features.auth.routing

import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.common.utils.asSuccessApplicationResponse
import com.planify.planifyspring.main.features.auth.domain.entities.AuthContext
import com.planify.planifyspring.main.features.auth.domain.use_cases.AuthUseCaseGroup
import com.planify.planifyspring.main.features.auth.routing.dto.UserPrivateDTO
import com.planify.planifyspring.main.features.auth.routing.dto.get_all_users.GetAllUsersResponseDTO
import com.planify.planifyspring.main.features.auth.routing.dto.me.GetMeResponseDTO
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UsersFeatureController(
    val authUseCaseGroup: AuthUseCaseGroup
) {
    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal authContext: AuthContext
    ): ResponseEntity<ApplicationResponse<GetMeResponseDTO>> {
        return ResponseEntity.ok(
            GetMeResponseDTO(
                user = UserPrivateDTO.fromEntity(user = authContext.user)
            ).asSuccessApplicationResponse()
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('READ_ANY_USER')")
    @GetMapping("/all")
    fun getAll(
        @PageableDefault(size = 10) pageable: Pageable,
    ): ResponseEntity<ApplicationResponse<GetAllUsersResponseDTO>> {
        val usersPaginated = authUseCaseGroup.getAllUsersPaginated(pageable)
        return ResponseEntity.ok(
            GetAllUsersResponseDTO(
                users = usersPaginated.map { UserPrivateDTO.fromEntity(user = it) }
            ).asSuccessApplicationResponse()
        )
    }
}