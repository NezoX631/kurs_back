package com.planify.planifyspring.main.features.meetings.domain.repositories

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingInviteParchSchema
import java.time.Instant

interface MeetingInvitesRepository {
    fun createInvite(meetingId: Long, senderId: Long, targetId: Long, expiresAt: Instant): MeetingInvite

    fun getInvite(uuid: String): MeetingInvite?

    fun updateInvite(inviteUuid: String, patch: MeetingInviteParchSchema)

    fun getMeetingInvites(meetingId: Long): List<MeetingInvite>
}
