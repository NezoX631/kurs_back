package com.planify.planifyspring.main.features.meetings.domain.services_impl
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus
import com.planify.planifyspring.core.exceptions.NotFoundAppError
import com.planify.planifyspring.main.common.utils.ObjectMapperHelper
import com.planify.planifyspring.main.features.actions.domain.services.ActionsService
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite

import com.planify.planifyspring.main.features.meetings.domain.repositories.MeetingInvitesRepository
import com.planify.planifyspring.main.features.meetings.domain.schemas.InviteRescheduleStatusDataScheme
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingInviteParchSchema
import com.planify.planifyspring.main.features.meetings.domain.schemas.actions.*
import com.planify.planifyspring.main.features.meetings.domain.services.MeetingInvitesService
import com.planify.planifyspring.main.features.meetings.domain.services.MeetingsService
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MeetingInvitesServiceImpl(
    val meetingInvitesRepository: MeetingInvitesRepository,
    val actionsService: ActionsService,
    val meetingService: MeetingsService,
    val objectMapperHelper: ObjectMapperHelper
) : MeetingInvitesService {
    override fun createInvite(meetingId: Long, senderId: Long, targetId: Long, expiresAt: Instant): MeetingInvite {
        val invite = meetingInvitesRepository.createInvite(meetingId, senderId, targetId, expiresAt)
    
        actionsService.createUserAction(
            userId = targetId,
            type = "meetings:invited",
            data = UserActionInvitedToMeetingSchema(
                senderId = senderId,
                targetId = targetId,
                meetingId = meetingId,
                inviteUuid = invite.uuid,
                createdAt = invite.createdAt
            )
        )

        actionsService.createAction(
            scope = "meeting:$meetingId",
            type = "meetings:meeting:invited",
            data = MeetingActionUserInvitedSchema(
                senderId = senderId,
                targetId = meetingId,
                meetingId = meetingId,
                inviteUuid = invite.uuid,
                createdAt = invite.createdAt
            )
        )

        return invite
    }

    override fun getInvite(inviteUuid: String): MeetingInvite {
        return meetingInvitesRepository.getInvite(inviteUuid) ?: throw NotFoundAppError("Invite was not found")
    }

    override fun getMeetingInvites(meetingId: Long): List<MeetingInvite> {
        return meetingInvitesRepository.getMeetingInvites(meetingId)
    }

    override fun acceptInvite(invite: MeetingInvite) {
        meetingInvitesRepository.updateInvite(
            inviteUuid = invite.uuid,
            patch = MeetingInviteParchSchema(
                status = MeetingInviteStatus.ACCEPTED
            )
        )

        meetingService.createMeetingParticipant(invite.meetingId, invite.targetId)

        actionsService.createUserAction(
            userId = invite.senderId,
            type = "meetings:invite_status_updated",
            data = UserActionInviteStatusUpdatedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                oldStatus = invite.status,  // Still contain old status!
                newStatus = MeetingInviteStatus.ACCEPTED,
            )
        )

        actionsService.createAction(
            scope = "meeting:${invite.meetingId}",
            type = "meetings:meeting:invite_status_updated",
            data = MeetingActionInviteStatusUpdatedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                oldStatus = invite.status,  // Still contain old status!
                newStatus = MeetingInviteStatus.ACCEPTED
            )
        )

        actionsService.createAction(
            scope = "meeting:${invite.meetingId}",
            type = "meetings:meeting:new_participant",
            data = MeetingActionNewParticipantSchema(
                meetingId = invite.meetingId,
                newParticipantId = invite.targetId,
                joinedAt = Instant.now()
            )
        )
    }

    override fun acceptInvite(inviteUuid: String) {
        acceptInvite(getInvite(inviteUuid))
    }

    override fun rejectInvite(invite: MeetingInvite) {
        meetingInvitesRepository.updateInvite(
            inviteUuid = invite.uuid,
            patch = MeetingInviteParchSchema(
                status = MeetingInviteStatus.REJECTED
            )
        )

        actionsService.createUserAction(
            userId = invite.senderId,
            type = "meetings:invite_status_updated",
            data = UserActionInviteStatusUpdatedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                oldStatus = invite.status,  // Still contain old status!
                newStatus = MeetingInviteStatus.REJECTED,
            )
        )

        actionsService.createAction(
            scope = "meeting:${invite.meetingId}",
            type = "meetings:meeting:invite_status_updated",
            data = MeetingActionInviteStatusUpdatedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                oldStatus = invite.status,  // Still contain old status!
                newStatus = MeetingInviteStatus.REJECTED
            )
        )
    }

    override fun rejectInvite(inviteUuid: String) {
        acceptInvite(getInvite(inviteUuid))
    }

    override fun requestRescheduleInvite(invite: MeetingInvite, rescheduleTo: Instant) {
        meetingInvitesRepository.updateInvite(
            inviteUuid = invite.uuid,
            patch = MeetingInviteParchSchema(
                status = MeetingInviteStatus.RESCHEDULE_REQUESTED,
                statusData = InviteRescheduleStatusDataScheme(
                    rescheduleTo = rescheduleTo
                )
            )
        )

        actionsService.createUserAction(
            userId = invite.senderId,
            type = "meetings:invite_reschedule_requested",
            data = UserActionInviteRescheduleRequestedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                rescheduleTo = rescheduleTo
            )
        )

        actionsService.createAction(
            scope = "meeting:${invite.meetingId}",
            type = "meetings:meeting:invite_reschedule_requested",
            data = MeetingActionInviteRescheduleRequestedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                rescheduleTo = rescheduleTo
            )
        )
    }

    override fun requestRescheduleInvite(inviteUuid: String, rescheduleTo: Instant) {
        requestRescheduleInvite(getInvite(inviteUuid), rescheduleTo)
    }

    override fun responseRescheduleInvite(invite: MeetingInvite, shouldReschedule: Boolean) {
        meetingInvitesRepository.updateInvite(
            inviteUuid = invite.uuid,
            patch = MeetingInviteParchSchema(
                status = MeetingInviteStatus.PENDING
            )
        )

        if (shouldReschedule) {
            @Suppress("UNCHECKED_CAST")  // TODO: Refactor it
            val rescheduleTo = objectMapperHelper.convertFromStringsMap(invite.statusData!! as Map<String, String>, InviteRescheduleStatusDataScheme::class.java).rescheduleTo
            meetingService.rescheduleMeeting(meetingId = invite.meetingId, rescheduleTo = rescheduleTo)
        }

        actionsService.createUserAction(
            userId = invite.targetId,
            type = "meetings:invite_reschedule_responded",
            data = UserActionInviteRescheduleRespondedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                shouldReschedule = shouldReschedule
            )
        )

        actionsService.createAction(
            scope = "meeting:${invite.meetingId}",
            type = "meetings:meeting:invite_reschedule_responded",
            data = MeetingActionInviteRescheduleRespondedSchema(
                meetingId = invite.meetingId,
                senderId = invite.senderId,
                targetId = invite.targetId,
                inviteUuid = invite.uuid,
                updatedAt = Instant.now(),
                shouldReschedule = shouldReschedule
            )
        )
    }

    override fun responseRescheduleInvite(inviteUuid: String, shouldReschedule: Boolean) {
        responseRescheduleInvite(getInvite(inviteUuid), shouldReschedule)
    }
}
