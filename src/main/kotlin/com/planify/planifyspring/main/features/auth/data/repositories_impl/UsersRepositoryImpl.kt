package com.planify.planifyspring.main.features.auth.data.repositories_impl

import com.planify.planifyspring.core.exceptions.AlreadyExistsAppError
import com.planify.planifyspring.main.common.utils.SecurityHelper
import com.planify.planifyspring.main.features.auth.data.jpa.UserJpaRepository
import com.planify.planifyspring.main.features.auth.data.models.UserModel
import com.planify.planifyspring.main.features.auth.domain.entities.AccessInfo
import com.planify.planifyspring.main.features.auth.domain.entities.User
import com.planify.planifyspring.main.features.auth.domain.repositories.UsersRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UsersRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UsersRepository {
    override fun create(username: String, email: String, passwordHash: String): User {
        val model = UserModel(
            username = username,
            email = email,
            passwordHash = passwordHash
        )

        if (userJpaRepository.existsByEmailAndUsername(email, username)) {  // TODO: Make faster check
            throw AlreadyExistsAppError("User with this email or username already exists")
        }

        userJpaRepository.save(model)

        return model.toEntity()
    }

    override fun save(user: User) {
        userJpaRepository.save(UserModel.fromEntity(entity = user))
    }

    override fun getById(id: Long): User? {
        return userJpaRepository.findByIdOrNull(id)?.toEntity()
    }

    override fun getByIdWithAccessInfo(id: Long): Pair<User, AccessInfo>? {
        val model = userJpaRepository.findByIdWithRolesAndAuthorities(id) ?: return null
        return model.toEntity() to model.getAccessInfo()
    }

    override fun getAllUsersPaginated(pageable: Pageable): Page<User> {
        return userJpaRepository.findAll(pageable).map { it.toEntity() }
    }

    override fun getByAuthCredentials(email: String, passwordRaw: String): User? {
        val model = userJpaRepository.findByEmail(email) ?: return null
        if (!SecurityHelper.isPasswordsMatch(passwordRaw, model.passwordHash)) return null
        return model.toEntity()
    }

    override fun getByAuthCredentialsWithAccessInfo(
        email: String,
        passwordRaw: String
    ): Pair<User, AccessInfo>? {
        val model = userJpaRepository.findByEmailWithRolesAndAuthorities(email) ?: return null
        if (!SecurityHelper.isPasswordsMatch(passwordRaw, model.passwordHash)) return null

        return model.toEntity() to model.getAccessInfo()
    }
}
