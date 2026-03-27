package com.planify.planifyspring.main.features.meetings.routing.dto.get_my_meetings

import com.planify.planifyspring.main.features.meetings.routing.dto.MeetingContextDTO
import java.time.Instant

data class GetMyMeetingsResponseDTO(
    val meetings: Map<Instant, List<MeetingContextDTO>>
)
