package com.planify.planifyspring.main.features.auth.data.jpa

import com.planify.planifyspring.main.features.auth.data.models.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserModel, Long> {
    @Query(
        """
        SELECT DISTINCT u
        FROM UserModel u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.authorities ra
        LEFT JOIN FETCH u.authorities ua
        WHERE u.id = :id
    """
    )
    fun findByIdWithRolesAndAuthorities(@Param("id") id: Long): UserModel?

    @Query(
        """
        SELECT DISTINCT u
        FROM UserModel u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.authorities ra
        LEFT JOIN FETCH u.authorities ua
        WHERE u.email = :email
    """
    )
    fun findByEmailWithRolesAndAuthorities(@Param("email") email: String): UserModel?

    fun findByEmail(email: String): UserModel?

    fun existsByEmailAndUsername(email: String, username: String): Boolean
}
