package com.planify.planifyspring.main.features.meetings.domain.schemas.actions

import java.time.Instant

data class UserActionInvitedToMeetingSchema(
    val senderId: Long,
    val targetId: Long,
    val meetingId: Long,
    val inviteUuid: String,
    val createdAt: Instant
)