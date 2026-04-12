package com.planify.planifyspring.main.features.profiles.data.jpa

import com.planify.planifyspring.main.features.profiles.data.models.ProfileModel
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProfilesJpaRepository : JpaRepository<ProfileModel, Long>, JpaSpecificationExecutor<ProfileModel> {
    fun findByUserId(userId: Long): ProfileModel?

    @Query(
        """
        SELECT p FROM ProfileModel p
        JOIN UserModel u ON p.userId = u.id
        WHERE (u.isActive = true OR u.isActive IS NULL)
        AND (
            :query = '' OR
            LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(COALESCE(p.department, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(COALESCE(p.position, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        """
    )
    fun searchProfiles(@Param("query") query: String, pageable: Pageable): Page<ProfileModel>

    @Modifying
    @Transactional
    @Query(
        """
        UPDATE ProfileModel p
        SET
            p.firstName = COALESCE(:firstName, p.firstName),
            p.lastName = COALESCE(:lastName, p.lastName),
            p.position = COALESCE(:position, p.position),
            p.department = COALESCE(:department, p.department),
            p.profileImageUrl = COALESCE(:profileImageUrl, p.profileImageUrl)
        WHERE p.userId = :userId
    """
    )
    fun parchProfile(
        userId: Long,
        firstName: String?,
        lastName: String?,
        position: String?,
        department: String?,
        profileImageUrl: String?
    )
}
