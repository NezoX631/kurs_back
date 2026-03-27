package com.planify.planifyspring.main.features.meetings.routing.get_meeting_with_context

import com.planify.planifyspring.main.features.meetings.routing.dto.MeetingContextDTO

data class GetMeetingWithContextResponseDTO(
    val meetingContext: MeetingContextDTO
)
