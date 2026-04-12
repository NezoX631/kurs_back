package com.planify.planifyspring.main.features.auth.data.repositories_impl

import com.planify.planifyspring.main.features.auth.data.jpa.RoleJpaRepository
import com.planify.planifyspring.main.features.auth.data.models.RoleModel
import com.planify.planifyspring.main.features.auth.domain.entities.Role
import com.planify.planifyspring.main.features.auth.domain.repositories.RolesRepository
import org.springframework.stereotype.Repository

@Repository
class RolesRepositoryImpl(
    private val roleJpaRepository: RoleJpaRepository
) : RolesRepository {
    override fun findOrCreateRole(name: String): Role {
        val existing = roleJpaRepository.findByName(name)
        if (existing != null) return existing.toEntity()
        return roleJpaRepository.save(RoleModel(name = name)).toEntity()
    }

    override fun getRoleByName(name: String): Role? {
        return roleJpaRepository.findByName(name)?.toEntity()
    }
}
