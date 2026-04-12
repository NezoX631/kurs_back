package com.planify.planifyspring.main.features.profiles.data.models

import com.planify.planifyspring.main.features.auth.data.models.UserModel
import com.planify.planifyspring.main.features.profiles.domain.entiries.Profile
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "profiles")
open class ProfileModel(
    @Id
    @Column(nullable = false, unique = true)
    open val userId: Long,

    open val firstName: String,

    open val lastName: String,

    @Column(unique = true)
    open val position: String?,

    @Column(unique = true)
    open val department: String?,

    open val profileImageUrl: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    open val user: UserModel? = null
) {
    fun toEntity(): Profile {
        return Profile(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            position = position,
            department = department,
            profileImageUrl = profileImageUrl
        )
    }
}
