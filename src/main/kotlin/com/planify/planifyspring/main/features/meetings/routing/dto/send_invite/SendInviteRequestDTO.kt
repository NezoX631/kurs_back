package com.planify.planifyspring.main.features.meetings.routing.dto.send_invite

data class SendInviteRequestDTO(
    val meetingId: Long,
    val targetId: Long
)
