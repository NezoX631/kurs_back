package com.planify.planifyspring.main.features.meetings.domain.schemas.actions

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus
import java.time.Instant

class MeetingActionInviteStatusUpdatedSchema(
    val oldStatus: MeetingInviteStatus,
    val newStatus: MeetingInviteStatus,

    val meetingId: Long,
    val senderId: Long,
    val targetId: Long,
    val inviteUuid: String,
    val updatedAt: Instant
)
