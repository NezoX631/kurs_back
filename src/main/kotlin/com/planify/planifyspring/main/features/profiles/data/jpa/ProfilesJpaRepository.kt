package com.planify.planifyspring.main.features.profiles.data.jpa

import com.planify.planifyspring.main.features.profiles.data.models.ProfileModel
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProfilesJpaRepository : JpaRepository<ProfileModel, Long>, JpaSpecificationExecutor<ProfileModel> {
    fun findByUserId(userId: Long): ProfileModel?

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
