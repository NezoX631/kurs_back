package com.planify.planifyspring.main.features.actions.routing

import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.common.utils.asSuccessApplicationResponse
import com.planify.planifyspring.main.features.actions.domain.use_cases.ActionsUseCaseGroup
import com.planify.planifyspring.main.features.actions.routing.dto.ActionDTO
import com.planify.planifyspring.main.features.actions.routing.dto.get_my_incomming_actions.GetMyIncomingActionsResponseDTO
import com.planify.planifyspring.main.features.auth.domain.entities.AuthContext
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/actions")
class ActionsController(
    val actionsUseCaseGroup: ActionsUseCaseGroup
) {
    @GetMapping("/my/incoming")
    fun getIncomingActions(
        @AuthenticationPrincipal authContext: AuthContext,
        @RequestParam lastSeen: String,
        @RequestParam count: Long = 10,
        @RequestParam timeout: Long = 30,
    ): ResponseEntity<ApplicationResponse<GetMyIncomingActionsResponseDTO>> {
        val actions = actionsUseCaseGroup.getUserIncomingActions(
            userId = authContext.user.id,
            lastSeen = lastSeen,
            count = count,
            timeout = timeout
        )

        return ResponseEntity.ok(
            GetMyIncomingActionsResponseDTO(
                actions = actions.map { ActionDTO.fromEntity(it) }
            ).asSuccessApplicationResponse()
        )
    }

    @DeleteMapping("/my/{actionId}")
    fun deleteAction(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable actionId: String,
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        actionsUseCaseGroup.deleteUserAction(
            userId = authContext.user.id,
            actionId = actionId
        )

        return ResponseEntity.ok(ApplicationResponse.success())
    }
}
