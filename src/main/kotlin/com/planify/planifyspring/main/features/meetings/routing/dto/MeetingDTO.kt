package com.planify.planifyspring.main.features.meetings.routing.dto

import com.planify.planifyspring.main.features.meetings.domain.entities.Meeting

data class MeetingDTO(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val description: String,
    val location: String,
    val startsAt: String,
    val duration: Int
) {
    companion object {
        fun fromEntity(entity: Meeting): MeetingDTO {
            return MeetingDTO(
                id = entity.id,
                ownerId = entity.ownerId,
                name = entity.name,
                description = entity.description,
                location = entity.location,
                startsAt = entity.startsAt.toString(),
                duration = entity.duration
            )
        }
    }
}
