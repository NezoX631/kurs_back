package com.planify.planifyspring.main.features.meetings.domain.schemas.actions

import java.time.Instant

class UserActionInviteRescheduleRequestedSchema(
    val meetingId: Long,
    val senderId: Long,
    val targetId: Long,
    val inviteUuid: String,
    val updatedAt: Instant,
    val rescheduleTo: Instant
)
