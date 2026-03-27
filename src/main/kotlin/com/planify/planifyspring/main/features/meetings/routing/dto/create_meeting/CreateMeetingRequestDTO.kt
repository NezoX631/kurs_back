package com.planify.planifyspring.main.features.meetings.routing.dto.create_meeting

import java.time.Instant

data class CreateMeetingRequestDTO(
    val name: String,
    val description: String,
    val location: String,
    val startsAt: Instant,
    val duration: Int
)