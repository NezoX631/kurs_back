package com.planify.planifyspring.main.features.meetings.domain.schemas

import java.time.Instant

data class MeetingPatchSchema(
    val name: String? = null,
    val description: String? = null,
    val location: String? = null,
    val startsAt: Instant? = null,
    val duration: Int? = null
)
