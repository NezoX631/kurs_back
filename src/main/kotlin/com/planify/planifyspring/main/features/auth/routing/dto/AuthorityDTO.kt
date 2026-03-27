package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.Authority

class AuthorityDTO(
    val id: Long,
    val name: String
) {
    companion object {
        fun fromEntity(entity: Authority): AuthorityDTO {
            return AuthorityDTO(
                id = entity.id,
                name = entity.name
            )
        }
    }
}
