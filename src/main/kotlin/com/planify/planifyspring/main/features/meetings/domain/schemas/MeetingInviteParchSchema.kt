package com.planify.planifyspring.main.features.meetings.domain.schemas

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus

class MeetingInviteParchSchema(
    val status: MeetingInviteStatus? = null,
    val statusData: Any? = null
)
