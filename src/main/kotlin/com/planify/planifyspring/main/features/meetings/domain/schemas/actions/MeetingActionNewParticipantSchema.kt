package com.planify.planifyspring.main.features.meetings.domain.schemas.actions

import java.time.Instant

data class MeetingActionNewParticipantSchema(
    val meetingId: Long,
    val newParticipantId: Long,
    val joinedAt: Instant
)
