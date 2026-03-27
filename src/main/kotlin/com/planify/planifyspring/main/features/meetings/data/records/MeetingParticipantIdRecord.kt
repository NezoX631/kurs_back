package com.planify.planifyspring.main.features.meetings.data.records

import com.planify.planifyspring.main.features.meetings.data.models.MeetingModel

data class MeetingParticipantIdRecord(
    val meeting: MeetingModel,
    val participantId: Long
)
