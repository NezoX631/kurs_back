package com.planify.planifyspring.main.features.auth.data.models

import com.planify.planifyspring.main.features.auth.domain.entities.Authority
import jakarta.persistence.*

@Entity
@Table(name = "authorities")
open class AuthorityModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    open val name: String,

    @ManyToMany(mappedBy = "authorities")
    open val users: MutableSet<UserModel> = mutableSetOf(),

    @ManyToMany(mappedBy = "authorities")
    open val roles: MutableSet<RoleModel> = mutableSetOf(),
) {
    fun toEntity(): Authority {
        return Authority(
            id = id!!,
            name = name
        )
    }
}

