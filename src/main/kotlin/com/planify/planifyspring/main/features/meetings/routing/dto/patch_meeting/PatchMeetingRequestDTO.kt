package com.planify.planifyspring.main.features.meetings.routing.dto.patch_meeting

import java.time.Instant

data class PatchMeetingRequestDTO(
    val name: String? = null,
    val description: String? = null,
    val location: String? = null,
    val startsAt: Instant? = null,
    val duration: Int? = null
)
