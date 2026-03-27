package com.planify.planifyspring.main.features.meetings.domain.entities

import java.time.Instant

data class MeetingInvite(
    val uuid: String,
    val meetingId: Long,
    val senderId: Long,
    val targetId: Long,
    val status: MeetingInviteStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    val expiresAt: Instant,
    val statusData: Any? = null
)
