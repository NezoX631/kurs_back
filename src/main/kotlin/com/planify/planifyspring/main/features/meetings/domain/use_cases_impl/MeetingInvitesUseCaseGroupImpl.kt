package com.planify.planifyspring.main.features.meetings.domain.use_cases_impl

import com.planify.planifyspring.core.exceptions.NotFoundAppError
import com.planify.planifyspring.main.exceptions.generics.BadRequestHttpException
import com.planify.planifyspring.main.exceptions.generics.ForbiddenHttpException
import com.planify.planifyspring.main.exceptions.generics.NotFoundHttpException
import com.planify.planifyspring.main.features.meetings.domain.entities.Meeting
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus
import com.planify.planifyspring.main.features.meetings.domain.services.MeetingInvitesService
import com.planify.planifyspring.main.features.meetings.domain.services.MeetingsService
import com.planify.planifyspring.main.features.meetings.domain.use_cases.MeetingInvitesUseCaseGroup
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class MeetingInvitesUseCaseGroupImpl(
    val meetingInvitesService: MeetingInvitesService,
    val meetingsService: MeetingsService,
) : MeetingInvitesUseCaseGroup {
    override fun getInvite(inviteUuid: String, requesterId: Long): MeetingInvite {
        val invite: MeetingInvite

        try {
            invite = meetingInvitesService.getInvite(inviteUuid)
        } catch (_: NotFoundAppError) {
            throw NotFoundHttpException("Invite was not found")
        }

//        if (
//            requesterId != invite.senderId &&
//            requesterId != invite.targetId &&
//            !meetingsService.isUserParticipant(requesterId, invite.meetingId)
//        ) throw ForbiddenHttpException("Cannot get invite info: you are not participant of this meeting")

        return invite
    }

    override fun getMeetingInvites(meetingId: Long, requesterId: Long): List<MeetingInvite> {
        val invites = meetingInvitesService.getMeetingInvites(meetingId)
//        if (!meetingsService.isUserParticipant(requesterId, meetingId)) throw ForbiddenHttpException("Cannot get invites info: you are not participant of this meeting")

        return invites
    }

    private fun createInvite(
        meeting: Meeting,
        senderId: Long,
        targetId: Long,
    ): MeetingInvite {
        val invites = getMeetingInvites(meeting.id, senderId)
        if (invites.firstOrNull { it.targetId == targetId } != null) throw BadRequestHttpException("Cannot invite user: target already has an invite to this meeting")

        if (senderId != meeting.ownerId) throw ForbiddenHttpException("Cannot invite user: you are not owner of this meeting")
        if (meetingsService.isUserParticipant(targetId, meeting.id)) throw BadRequestHttpException("Cannot invite user: target already participant of this meeting")
        if (meeting.startsAt < Instant.now()) throw BadRequestHttpException("Cannot invite user: Meeting has already started")

        return meetingInvitesService.createInvite(meeting.id, senderId, targetId, expiresAt = meeting.startsAt)
    }

    override fun createInvite(
        meetingId: Long,
        senderId: Long,
        targetId: Long,
    ): MeetingInvite {
        val meeting: Meeting

        try {
            meeting = meetingsService.getMeetingById(meetingId)
        } catch (_: NotFoundAppError) {
            throw NotFoundHttpException("Cannot invite user: Meeting was not found")
        }

        return createInvite(meeting, senderId, targetId)
    }

    private fun acceptInvite(invite: MeetingInvite, meeting: Meeting, requesterId: Long) {
        if (invite.targetId != requesterId) throw ForbiddenHttpException("Cannot accept invite: you are not target of this invite")

        if (
            invite.status == MeetingInviteStatus.ACCEPTED ||
            invite.status == MeetingInviteStatus.REJECTED
        ) throw BadRequestHttpException("Cannot accept invite: Invite has already been replied")

        if (invite.expiresAt < Instant.now()) throw BadRequestHttpException("Cannot accept invite: invite is expired")
        if (
            meetingsService.userHasMeetingsBetween(
                userId = invite.targetId,
                startAt = meeting.startsAt,
                endAt = meeting.startsAt.plusSeconds(meeting.duration * 3600L)
            )
        ) throw BadRequestHttpException("User already has meeting at this time interval")

        meetingInvitesService.acceptInvite(invite)
    }

    override fun acceptInvite(inviteUuid: String, requesterId: Long) {
        val invite = getInvite(inviteUuid, requesterId)

        val meeting: Meeting

        try {
            meeting = meetingsService.getMeetingById(invite.meetingId)
        } catch (_: NotFoundAppError) {
            throw NotFoundHttpException("Cannot invite user: Meeting was not found")
        }

        acceptInvite(invite, meeting, requesterId)
    }

    private fun rejectInvite(invite: MeetingInvite, requesterId: Long) {
        if (invite.targetId != requesterId) throw ForbiddenHttpException("Cannot reject invite: you are not target of this invite")

        if (
            invite.status == MeetingInviteStatus.ACCEPTED ||
            invite.status == MeetingInviteStatus.REJECTED
        ) throw BadRequestHttpException("Cannot reject invite: Invite has already been replied")

        if (invite.expiresAt < Instant.now()) throw BadRequestHttpException("Cannot reject invite: invite is expired")

        meetingInvitesService.rejectInvite(invite)
    }

    override fun rejectInvite(inviteUuid: String, requesterId: Long) {
        val invite = getInvite(inviteUuid, requesterId)  // Also checks if invite exists and user can access its info
        rejectInvite(invite, requesterId)
    }

    private fun requestRescheduleInvite(invite: MeetingInvite, rescheduleTo: Instant, requesterId: Long) {
        if (invite.targetId != requesterId) throw ForbiddenHttpException("Cannot request reschedule: you are not target of this invite")

        if (
            invite.status == MeetingInviteStatus.ACCEPTED ||
            invite.status == MeetingInviteStatus.REJECTED
        ) throw BadRequestHttpException("Cannot request reschedule: Invite has already been replied")

        if (invite.expiresAt < Instant.now()) throw BadRequestHttpException("Cannot request reschedule: invite is expired")

        meetingInvitesService.requestRescheduleInvite(invite, rescheduleTo)
    }

    override fun requestRescheduleInvite(inviteUuid: String, rescheduleTo: Instant, requesterId: Long) {
        val invite = getInvite(inviteUuid, requesterId)  // Also checks if invite exists and user can access its info
        requestRescheduleInvite(invite, rescheduleTo, requesterId)
    }

    private fun responseRescheduleInvite(invite: MeetingInvite, shouldReschedule: Boolean, requesterId: Long) {
        if (invite.senderId != requesterId) throw ForbiddenHttpException("Cannot response reschedule: you are not sender of this invite")

        if (invite.status != MeetingInviteStatus.RESCHEDULE_REQUESTED) throw BadRequestHttpException("Reschedule is not requested")

        if (invite.expiresAt < Instant.now()) throw BadRequestHttpException("Cannot response reschedule: invite is expired")

        meetingInvitesService.responseRescheduleInvite(invite, shouldReschedule)
    }

    override fun responseRescheduleInvite(inviteUuid: String, shouldReschedule: Boolean, requesterId: Long) {
        val invite = getInvite(inviteUuid, requesterId)  // Also checks if invite exists and user can access its info
        return responseRescheduleInvite(invite, shouldReschedule, requesterId)
    }
}
