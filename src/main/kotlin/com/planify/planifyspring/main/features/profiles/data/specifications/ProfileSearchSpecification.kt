package com.planify.planifyspring.main.features.profiles.data.specifications

import com.planify.planifyspring.main.features.auth.data.models.UserModel
import com.planify.planifyspring.main.features.profiles.data.models.ProfileModel
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object ProfileSearchSpecification {
    fun searchProfile(input: String): Specification<ProfileModel> {
        val tokens = input.trim().lowercase().split("\\s+".toRegex())

        return Specification { root: Root<ProfileModel>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            var finalPredicate = criteriaBuilder.disjunction()

            val userRoot: Root<UserModel> = query.from(UserModel::class.java)
            val joinCondition = criteriaBuilder.equal(root.get<Long>("userId"), userRoot.get<Long>("id"))

            tokens.forEach { token ->
                val pattern = "%$token%"

                val tokenPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("department")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(userRoot.get("username")), pattern)
                )

                finalPredicate = criteriaBuilder.or(finalPredicate, criteriaBuilder.and(joinCondition, tokenPredicate))
            }

            finalPredicate
        }
    }
}
