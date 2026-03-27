package com.planify.planifyspring.main.features.auth.data.models

import com.planify.planifyspring.main.features.auth.domain.entities.Role
import jakarta.persistence.*

@Entity
@Table(name = "roles")
open class RoleModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    open val name: String,

    @ManyToMany(mappedBy = "roles")
    open val users: MutableSet<UserModel> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_authorities",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")]
    )
    open val authorities: MutableSet<AuthorityModel> = mutableSetOf(),
) {
    fun toEntity(): Role {
        return Role(
            id = id!!,
            name = name
        )
    }
}
