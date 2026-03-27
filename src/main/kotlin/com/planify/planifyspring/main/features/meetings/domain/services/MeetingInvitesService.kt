package com.planify.planifyspring.main.features.meetings.domain.services

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import java.time.Instant

interface MeetingInvitesService {
    fun createInvite(meetingId: Long, senderId: Long, targetId: Long, expiresAt: Instant): MeetingInvite

    fun getInvite(inviteUuid: String): MeetingInvite

    fun getMeetingInvites(meetingId: Long): List<MeetingInvite>

    fun acceptInvite(invite: MeetingInvite)
    fun acceptInvite(inviteUuid: String)

    fun rejectInvite(invite: MeetingInvite)
    fun rejectInvite(inviteUuid: String)

    fun requestRescheduleInvite(invite: MeetingInvite, rescheduleTo: Instant)
    fun requestRescheduleInvite(inviteUuid: String, rescheduleTo: Instant)

    fun responseRescheduleInvite(invite: MeetingInvite, shouldReschedule: Boolean)
    fun responseRescheduleInvite(inviteUuid: String, shouldReschedule: Boolean)
}
