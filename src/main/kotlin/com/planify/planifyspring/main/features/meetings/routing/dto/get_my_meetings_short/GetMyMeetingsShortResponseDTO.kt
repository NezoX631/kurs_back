package com.planify.planifyspring.main.features.meetings.routing.dto.get_my_meetings_short

import java.time.Instant

data class GetMyMeetingsShortResponseDTO(
    val meetings: Map<Instant, Long>
)
