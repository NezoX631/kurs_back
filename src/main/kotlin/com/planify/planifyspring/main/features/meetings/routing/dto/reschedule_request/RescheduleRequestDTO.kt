package com.planify.planifyspring.main.features.meetings.routing.dto.reschedule_request

import java.time.LocalDateTime

data class RescheduleRequestDTO(
    val rescheduleTo: LocalDateTime
)
