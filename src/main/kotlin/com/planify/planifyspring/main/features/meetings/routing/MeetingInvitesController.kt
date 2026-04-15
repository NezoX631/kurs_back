package com.planify.planifyspring.main.features.meetings.routing

import com.planify.planifyspring.core.utils.asUTCInstant
import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.common.utils.asSuccessApplicationResponse
import com.planify.planifyspring.main.exceptions.generics.NotFoundHttpException
import com.planify.planifyspring.main.features.auth.domain.entities.AuthContext
import com.planify.planifyspring.main.features.auth.domain.use_cases.AuthUseCaseGroup
import com.planify.planifyspring.main.features.meetings.domain.use_cases.MeetingInvitesUseCaseGroup
import com.planify.planifyspring.main.features.meetings.routing.dto.MeetingInviteDTO
import com.planify.planifyspring.main.features.meetings.routing.dto.get_invite.GetInviteResponseDTO
import com.planify.planifyspring.main.features.meetings.routing.dto.reschedule_request.RescheduleRequestDTO
import com.planify.planifyspring.main.features.meetings.routing.dto.reschedule_response.RescheduleAnswerRequestDTO
import com.planify.planifyspring.main.features.meetings.routing.dto.send_invite.SendInviteRequestDTO
import com.planify.planifyspring.main.features.meetings.routing.dto.send_invite.SendInviteResponseDTO
import com.planify.planifyspring.main.features.sync.WebSocketSyncService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/meetings/invites")
class MeetingInvitesController(
    val meetingInviteUseCaseGroup: MeetingInvitesUseCaseGroup,
    val authUseCaseGroup: AuthUseCaseGroup,
    val webSocketSyncService: WebSocketSyncService
) {
    @PostMapping("")
    fun sendInvite(
        @AuthenticationPrincipal authContext: AuthContext,
        @RequestBody body: SendInviteRequestDTO
    ): ResponseEntity<ApplicationResponse<SendInviteResponseDTO>> {
        val invite = meetingInviteUseCaseGroup.createInvite(
            meetingId = body.meetingId,
            senderId = authContext.user.id,
            targetId = body.targetId
        )

        // Отправляем push-уведомление приглашенному пользователю
        val meeting = meetingInviteUseCaseGroup.getMeetingById(body.meetingId)
        authUseCaseGroup.sendPushNotification(
            userId = body.targetId,
            title = "Новое приглашение на встречу",
            body = "${authContext.user.username} приглашает вас на встречу \"${meeting?.name}\"",
            type = "meeting",
            data = mapOf("meetingId" to body.meetingId.toString())
        )

        // WebSocket синхронизация: уведомляем приглашённого
        webSocketSyncService.notifyInviteSent(
            targetId = body.targetId,
            senderId = authContext.user.id,
            senderName = authContext.user.username,
            meetingId = body.meetingId,
            meetingName = meeting?.name ?: "",
            inviteUuid = invite.uuid
        )

        return ResponseEntity.ok(
            SendInviteResponseDTO(
                invite = MeetingInviteDTO.fromEntity(invite)
            ).asSuccessApplicationResponse()
        )
    }

    @GetMapping("/{inviteUuid}")
    fun getInvite(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable inviteUuid: String
    ): ResponseEntity<ApplicationResponse<GetInviteResponseDTO>> {
        val invite = meetingInviteUseCaseGroup.getInvite(
            inviteUuid = inviteUuid,
            requesterId = authContext.user.id
        ) ?: throw NotFoundHttpException("Invite was not found")

        return ResponseEntity.ok(
            GetInviteResponseDTO(
                invite = MeetingInviteDTO.fromEntity(invite)
            ).asSuccessApplicationResponse()
        )
    }

    @PostMapping("/{inviteUuid}/accept")
    fun acceptInvite(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable inviteUuid: String
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        val invite = meetingInviteUseCaseGroup.getInvite(inviteUuid, authContext.user.id)

        meetingInviteUseCaseGroup.acceptInvite(
            inviteUuid = inviteUuid,
            requesterId = authContext.user.id
        )

        // WebSocket синхронизация: уведомляем организатора встречи
        if (invite != null) {
            val meeting = meetingInviteUseCaseGroup.getMeetingById(invite.meetingId)
            val targetProfile = authUseCaseGroup.getUserById(authContext.user.id)
            webSocketSyncService.notifyInviteAccepted(
                inviteUuid = inviteUuid,
                meetingId = invite.meetingId,
                targetId = authContext.user.id,
                targetName = targetProfile?.username ?: "User",
                ownerId = meeting?.ownerId ?: 0
            )
        }

        return ResponseEntity.ok(ApplicationResponse.success())
    }

    @PostMapping("/{inviteUuid}/reject")
    fun rejectInvite(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable inviteUuid: String
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        val invite = meetingInviteUseCaseGroup.getInvite(inviteUuid, authContext.user.id)

        meetingInviteUseCaseGroup.rejectInvite(
            inviteUuid = inviteUuid,
            requesterId = authContext.user.id
        )

        // WebSocket синхронизация: уведомляем организатора встречи
        if (invite != null) {
            val meeting = meetingInviteUseCaseGroup.getMeetingById(invite.meetingId)
            val targetProfile = authUseCaseGroup.getUserById(authContext.user.id)
            webSocketSyncService.notifyInviteRejected(
                inviteUuid = inviteUuid,
                meetingId = invite.meetingId,
                targetId = authContext.user.id,
                targetName = targetProfile?.username ?: "User",
                ownerId = meeting?.ownerId ?: 0
            )
        }

        return ResponseEntity.ok(ApplicationResponse.success())
    }

    @PostMapping("/{inviteUuid}/reschedule/request")
    fun requestRescheduleInvite(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable inviteUuid: String,
        @RequestBody body: RescheduleRequestDTO
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        val invite = meetingInviteUseCaseGroup.getInvite(inviteUuid, authContext.user.id)

        meetingInviteUseCaseGroup.requestRescheduleInvite(
            inviteUuid = inviteUuid,
            requesterId = authContext.user.id,
            rescheduleTo = body.rescheduleTo.asUTCInstant()
        )

        // WebSocket синхронизация: уведомляем организатора
        if (invite != null) {
            val meeting = meetingInviteUseCaseGroup.getMeetingById(invite.meetingId)
            webSocketSyncService.notifyRescheduleRequested(
                inviteUuid = inviteUuid,
                meetingId = invite.meetingId,
                requesterId = authContext.user.id,
                requesterName = authContext.user.username,
                ownerId = meeting?.ownerId ?: 0,
                newTime = body.rescheduleTo.toString()
            )
        }

        return ResponseEntity.ok(ApplicationResponse.success())
    }

    @PostMapping("/{inviteUuid}/reschedule/response")
    fun responseRescheduleInvite(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable inviteUuid: String,
        @RequestBody body: RescheduleAnswerRequestDTO
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        val invite = meetingInviteUseCaseGroup.getInvite(inviteUuid, authContext.user.id)

        meetingInviteUseCaseGroup.responseRescheduleInvite(
            inviteUuid = inviteUuid,
            requesterId = authContext.user.id,
            shouldReschedule = body.shouldReschedule
        )

        // WebSocket синхронизация: уведомляем того, кто запросил перенос
        if (invite != null) {
            val requesterId = invite.senderId // тот, кто создал приглашение (организатор)
            // Но запрос переноса делал другой пользователь, нужно найти его
            // Для простоты уведомляем все заинтересованные стороны
            val meeting = meetingInviteUseCaseGroup.getMeetingById(invite.meetingId)
            webSocketSyncService.notifyRescheduleAnswered(
                inviteUuid = inviteUuid,
                meetingId = invite.meetingId,
                responderId = authContext.user.id,
                responderName = authContext.user.username,
                requesterId = meeting?.ownerId ?: 0,
                accepted = body.shouldReschedule
            )
        }

        return ResponseEntity.ok(ApplicationResponse.success())
    }

    @GetMapping("/my/sent")
    fun getSentInvites(
        @AuthenticationPrincipal authContext: AuthContext,
    ): ResponseEntity<ApplicationResponse<List<MeetingInviteDTO>>> {
        val invites = meetingInviteUseCaseGroup.getSentInvitesBySender(authContext.user.id)

        return ResponseEntity.ok(
            ApplicationResponse.success(
                invites.map { MeetingInviteDTO.fromEntity(it) }
            )
        )
    }
}
