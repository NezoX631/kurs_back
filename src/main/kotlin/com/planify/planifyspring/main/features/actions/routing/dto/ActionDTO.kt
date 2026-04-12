package com.planify.planifyspring.main.features.actions.routing.dto

import com.planify.planifyspring.main.features.actions.domain.entities.Action
import java.time.Instant

data class ActionDTO(
    val id: String,
    val type: String,
    val data: Any?,
    val createdAt: Instant
) {
    companion object {
        fun fromEntity(entity: Action): ActionDTO {
            return ActionDTO(
                id = entity.id,
                type = entity.type,
                data = entity.data,
                createdAt = entity.createdAt
            )
        }
    }
}
