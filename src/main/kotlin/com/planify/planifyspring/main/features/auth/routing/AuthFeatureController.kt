package com.planify.planifyspring.main.features.auth.routing

import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.features.auth.domain.use_cases.AuthUseCaseGroup
import com.planify.planifyspring.main.features.auth.routing.dto.*
import com.planify.planifyspring.main.features.auth.routing.dto.register.RegisterRequestDTO
import com.planify.planifyspring.main.features.auth.routing.dto.register.RegisterResponseDTO
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthFeatureController(
    private val authUseCaseGroup: AuthUseCaseGroup
) {

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequestDTO,
        @RequestHeader("User-Agent") userAgent: String
    ): ApplicationResponse<RegisterResponseDTO> {
        val result = authUseCaseGroup.register(
            username = request.username,
            email = request.email,
            passwordRaw = request.password,
            userAgent = userAgent,
            clientName = request.clientName,
            createProfileSchema = CreateProfileSchema(
                firstName = request.firstName,
                lastName = request.lastName,
                position = request.position,
                department = request.department,
                profileImageUrl = request.profileImageUrl
            ),
            sessionName = null
        )

        val dto = RegisterResponseDTO(
            user = RegisterResponseDTO.UserDTO(
                id = result.first.user.id,
                username = result.first.user.username,
                email = result.first.user.email
            ),
            session = AuthSessionPrivateDTO.fromDomain(result.first.session),
            tokens = TokensDTO.fromDomain(result.second),
            accessInfo = AccessInfoDTO.fromDomain(result.first.accessInfo)
        )

        return ApplicationResponse.success(dto)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequestDTO,
        @RequestHeader("User-Agent") userAgent: String
    ): ApplicationResponse<AuthContextDTO> {
        val result = authUseCaseGroup.login(
            email = request.email,
            passwordRaw = request.password,
            userAgent = userAgent,
            clientName = request.clientName,
            sessionName = null
        )
        val dto = AuthContextDTO(
            user = UserPrivateDTO.fromDomain(result.first.user),
            session = AuthSessionPrivateDTO.fromDomain(result.first.session),
            tokens = TokensDTO.fromDomain(result.second),
            accessInfo = AccessInfoDTO.fromDomain(result.first.accessInfo)
        )
        return ApplicationResponse.success(dto)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody request: RefreshTokenRequestDTO,
        @RequestHeader("User-Agent") userAgent: String
    ): ApplicationResponse<TokensDTO> {
        val result = authUseCaseGroup.refresh(
            refreshToken = request.refreshToken,
            currentUserAgent = userAgent
        )
        return ApplicationResponse.success(TokensDTO.fromDomain(result))
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: LogoutRequestDTO): ApplicationResponse<Nothing> {
        // Здесь можно добавить реальную логику выхода, если она есть
        println("Logout called for tokens: ${request.accessToken}, ${request.refreshToken}")
        return ApplicationResponse.success()
    }

    @GetMapping("/session/{sessionUuid}")
    fun getSession(
        @PathVariable sessionUuid: String,
        @RequestParam userId: Long
    ): ApplicationResponse<AuthSessionPrivateDTO> {
        val session = authUseCaseGroup.getSession(userId, sessionUuid)
        return ApplicationResponse.success(AuthSessionPrivateDTO.fromDomain(session))
    }

    @GetMapping("/sessions")
    fun getUserSessions(@RequestParam userId: Long): ApplicationResponse<List<AuthSessionPrivateDTO>> {
        val sessions = authUseCaseGroup.getUserSessions(userId)
        val dtoList = sessions.map { AuthSessionPrivateDTO.fromDomain(it) }
        return ApplicationResponse.success(dtoList)
    }

    @GetMapping("/sessions/active")
    fun getActiveUserSessions(@RequestParam userId: Long): ApplicationResponse<List<AuthSessionPrivateDTO>> {
        val sessions = authUseCaseGroup.getActiveUserSessions(userId)
        val dtoList = sessions.map { AuthSessionPrivateDTO.fromDomain(it) }
        return ApplicationResponse.success(dtoList)
    }

    @DeleteMapping("/session/{sessionUuid}")
    fun revokeSession(
        @PathVariable sessionUuid: String,
        @RequestParam userId: Long
    ): ApplicationResponse<Nothing> {
        authUseCaseGroup.revokeSession(userId, sessionUuid)
        return ApplicationResponse.success()
    }
}