package com.planify.planifyspring.main.features.auth.routing.dto

import com.planify.planifyspring.main.features.auth.domain.entities.AccessInfo

data class AccessInfoDTO(
    val authorities: List<AuthorityDTO>,
    val roles: List<RoleDTO>
) {
    companion object {
        fun fromDomain(info: AccessInfo): AccessInfoDTO {
            return AccessInfoDTO(
                authorities = info.authorities.map { AuthorityDTO.fromDomain(it) },
                roles = info.roles.map { RoleDTO.fromDomain(it) }
            )
        }
    }

    data class AuthorityDTO(
        val id: Long,
        val name: String
    ) {
        companion object {
            fun fromDomain(authority: com.planify.planifyspring.main.features.auth.domain.entities.Authority): AuthorityDTO {
                return AuthorityDTO(
                    id = authority.id,
                    name = authority.name
                )
            }
        }
    }

    data class RoleDTO(
        val id: Long,
        val name: String
    ) {
        companion object {
            fun fromDomain(role: com.planify.planifyspring.main.features.auth.domain.entities.Role): RoleDTO {
                return RoleDTO(
                    id = role.id,
                    name = role.name
                )
            }
        }
    }
}