package com.planify.planifyspring.main.features.auth.data.jpa

import com.planify.planifyspring.main.features.auth.data.models.AuthorityModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityJpaRepository : JpaRepository<AuthorityModel, Long> {
    fun findByName(name: String): AuthorityModel?
}
