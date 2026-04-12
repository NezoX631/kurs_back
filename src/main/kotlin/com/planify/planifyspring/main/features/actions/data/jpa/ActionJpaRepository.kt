package com.planify.planifyspring.main.features.actions.data.jpa

import com.planify.planifyspring.main.features.actions.data.models.ActionModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ActionJpaRepository : JpaRepository<ActionModel, Long> {
    fun findByUuid(uuid: String): ActionModel?

    fun findByScopeOrderByCreatedAtAsc(scope: String): List<ActionModel>

    @Query("SELECT a FROM ActionModel a WHERE a.scope = :scope AND a.recordId > :recordId ORDER BY a.createdAt ASC")
    fun findByScopeAndRecordIdGreaterThanOrderByCreatedAtAsc(
        @Param("scope") scope: String,
        @Param("recordId") recordId: String
    ): List<ActionModel>

    @Query("SELECT a FROM ActionModel a WHERE a.scope = :scope AND a.type = :type AND a.data LIKE %:inviteUuid% ORDER BY a.createdAt DESC")
    fun findByScopeAndTypeAndInviteUuid(
        @Param("scope") scope: String,
        @Param("type") type: String,
        @Param("inviteUuid") inviteUuid: String
    ): List<ActionModel>
}
