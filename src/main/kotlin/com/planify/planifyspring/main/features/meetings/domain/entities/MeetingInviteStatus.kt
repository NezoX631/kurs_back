package com.planify.planifyspring.main.features.meetings.domain.entities

enum class MeetingInviteStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    REJECTED,
    RESCHEDULE_REQUESTED,
    EXPIRED,
    CANCELLED
}