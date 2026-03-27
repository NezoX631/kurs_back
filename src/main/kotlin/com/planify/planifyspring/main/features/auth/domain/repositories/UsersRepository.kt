package com.planify.planifyspring.main.features.auth.domain.repositories

import com.planify.planifyspring.main.features.auth.domain.entities.AccessInfo
import com.planify.planifyspring.main.features.auth.domain.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UsersRepository {
    fun save(user: User)

    fun create(
        username: String,
        email: String,
        passwordHash: String
    ): User

    fun getById(id: Long): User?
    fun getByIdWithAccessInfo(id: Long): Pair<User, AccessInfo>?
    fun getAllUsersPaginated(pageable: Pageable): Page<User>

    fun getByAuthCredentials(email: String, passwordRaw: String): User?
    fun getByAuthCredentialsWithAccessInfo(email: String, passwordRaw: String): Pair<User, AccessInfo>?
}
