package com.planify.planifyspring.main.features.meetings.data.records

import java.time.LocalDate


data class DayMeetingsCountRecord(
    val date: LocalDate,
    val count: Long
)
