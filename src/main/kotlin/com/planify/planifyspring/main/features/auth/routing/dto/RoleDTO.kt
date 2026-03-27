package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.Role

class RoleDTO(
    val id: Long,
    val name: String
) {
    companion object {
        fun fromEntity(entity: Role): RoleDTO {
            return RoleDTO(
                id = entity.id,
                name = entity.name
            )
        }
    }
}
