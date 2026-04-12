package com.planify.planifyspring.main.features.auth.domain.repositories

import com.planify.planifyspring.main.features.auth.domain.entities.Role

interface RolesRepository {
    fun findOrCreateRole(name: String): Role
    fun getRoleByName(name: String): Role?
}
