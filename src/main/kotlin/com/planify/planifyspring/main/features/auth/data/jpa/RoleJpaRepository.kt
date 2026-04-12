package com.planify.planifyspring.main.features.auth.data.jpa

import com.planify.planifyspring.main.features.auth.data.models.RoleModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoleJpaRepository : JpaRepository<RoleModel, Long> {
    fun findByName(name: String): RoleModel?

    @Query("SELECT r FROM RoleModel r LEFT JOIN FETCH r.authorities WHERE r.id = :id")
    fun findByIdWithAuthorities(@Param("id") id: Long): RoleModel?
}
