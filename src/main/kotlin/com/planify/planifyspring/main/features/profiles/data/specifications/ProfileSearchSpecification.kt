package com.planify.planifyspring.main.features.profiles.data.specifications

import com.planify.planifyspring.main.features.auth.data.models.UserModel
import com.planify.planifyspring.main.features.profiles.data.models.ProfileModel
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object ProfileSearchSpecification {
    fun searchProfile(input: String): Specification<ProfileModel> {
        return Specification { root: Root<ProfileModel>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            // Явный INNER JOIN с users таблицей через relation "user"
            val userJoin: jakarta.persistence.criteria.Join<*, *> = root.join<Any, Any>("user", JoinType.INNER)

            val predicates = mutableListOf<Predicate>()

            // Всегда фильтруем только активных пользователей
            predicates.add(criteriaBuilder.isTrue(userJoin.get<Boolean>("isActive")))

            // Если query пустой — возвращаем всех активных пользователей
            if (input.isBlank()) {
                return@Specification criteriaBuilder.and(*predicates.toTypedArray())
            }

            val tokens = input.trim().lowercase().split(Regex("\\s+"))

            val tokenPredicates = mutableListOf<Predicate>()

            tokens.forEach { token ->
                val pattern = "%$token%"

                tokenPredicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get<String>("department")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get<String>("position")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get<String>("username")), pattern)
                    )
                )
            }

            predicates.add(criteriaBuilder.or(*tokenPredicates.toTypedArray()))

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}
