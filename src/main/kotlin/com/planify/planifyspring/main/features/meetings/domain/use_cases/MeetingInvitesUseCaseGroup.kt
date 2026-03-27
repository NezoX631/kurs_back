package com.planify.planifyspring.main.features.meetings.domain.use_cases

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import java.time.Instant

interface MeetingInvitesUseCaseGroup {
    fun createInvite(meetingId: Long, senderId: Long, targetId: Long): MeetingInvite

    fun getInvite(inviteUuid: String, requesterId: Long): MeetingInvite?

    fun getMeetingInvites(meetingId: Long, requesterId: Long): List<MeetingInvite>

    fun acceptInvite(inviteUuid: String, requesterId: Long)

    fun rejectInvite(inviteUuid: String, requesterId: Long)

    fun requestRescheduleInvite(inviteUuid: String, rescheduleTo: Instant, requesterId: Long)

    fun responseRescheduleInvite(inviteUuid: String, shouldReschedule: Boolean, requesterId: Long)
}
