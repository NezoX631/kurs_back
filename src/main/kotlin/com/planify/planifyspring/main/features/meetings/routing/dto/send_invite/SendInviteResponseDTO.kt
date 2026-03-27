package com.planify.planifyspring.main.features.meetings.routing.dto.send_invite

import com.planify.planifyspring.main.features.meetings.routing.dto.MeetingInviteDTO

data class SendInviteResponseDTO(
    val invite: MeetingInviteDTO
)
