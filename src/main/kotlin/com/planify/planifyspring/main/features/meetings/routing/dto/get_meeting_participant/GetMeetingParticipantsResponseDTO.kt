package com.planify.planifyspring.main.features.meetings.routing.dto.get_meeting_participant

import com.planify.planifyspring.main.features.meetings.routing.dto.MeetingDTO

data class GetMeetingParticipantsResponseDTO(
    val meeting: MeetingDTO,
    val participantIds: List<Long>
)
