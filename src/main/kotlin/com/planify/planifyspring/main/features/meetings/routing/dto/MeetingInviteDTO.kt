package com.planify.planifyspring.main.features.meetings.routing.dto

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus
import java.time.Instant

data class MeetingInviteDTO(
    val uuid: String,
    val meetingId: Long,
    val senderId: Long,
    val targetId: Long,
    val status: MeetingInviteStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: MeetingInvite): MeetingInviteDTO = MeetingInviteDTO(
            uuid = entity.uuid,
            meetingId = entity.meetingId,
            senderId = entity.senderId,
            targetId = entity.targetId,
            status = entity.status,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
