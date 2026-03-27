package com.planify.planifyspring.main.features.meetings.domain.schemas

import java.time.Instant

data class InviteRescheduleStatusDataScheme(
    val rescheduleTo: Instant,
)
