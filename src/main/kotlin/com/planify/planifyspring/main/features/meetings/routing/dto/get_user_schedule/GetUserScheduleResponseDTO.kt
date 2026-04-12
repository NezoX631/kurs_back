package com.planify.planifyspring.main.features.meetings.routing.dto.get_user_schedule

import com.fasterxml.jackson.annotation.JsonProperty

data class GetUserScheduleResponseDTO(
    @JsonProperty("schedule")
    val schedule: Map<Int, Boolean>
)