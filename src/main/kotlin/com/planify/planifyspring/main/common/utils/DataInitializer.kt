package com.planify.planifyspring.main.common.utils

import com.planify.planifyspring.main.features.auth.data.jpa.AuthorityJpaRepository
import com.planify.planifyspring.main.features.auth.data.jpa.RoleJpaRepository
import com.planify.planifyspring.main.features.auth.data.models.AuthorityModel
import com.planify.planifyspring.main.features.auth.data.models.RoleModel
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * Инициализирует базовые роли и полномочия в БД при старте приложения.
 */
@Component
class DataInitializer(
    private val roleJpaRepository: RoleJpaRepository,
    private val authorityJpaRepository: AuthorityJpaRepository
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(args: ApplicationArguments) {
        try {
            initRoles()
            initAuthorities()
            linkRolesToAuthorities()
            log.info("✅ Data initialization completed")
        } catch (e: Exception) {
            log.warn("⚠️ Data initialization skipped (roles may already exist): ${e.message}")
        }
    }

    private fun initRoles() {
        listOf("ROLE_ADMIN", "ROLE_USER").forEach { name ->
            if (roleJpaRepository.findByName(name) == null) {
                roleJpaRepository.save(RoleModel(name = name))
                log.info("  Created role: $name")
            }
        }
    }

    private fun initAuthorities() {
        listOf("READ_PRIVILEGE", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE").forEach { name ->
            if (authorityJpaRepository.findByName(name) == null) {
                authorityJpaRepository.save(AuthorityModel(name = name))
                log.info("  Created authority: $name")
            }
        }
    }

    private fun linkRolesToAuthorities() {
        val adminRole = roleJpaRepository.findByName("ROLE_ADMIN")
        val userRole = roleJpaRepository.findByName("ROLE_USER")
        val readAuth = authorityJpaRepository.findByName("READ_PRIVILEGE")
        val writeAuth = authorityJpaRepository.findByName("WRITE_PRIVILEGE")
        val deleteAuth = authorityJpaRepository.findByName("DELETE_PRIVILEGE")

        // ROLE_ADMIN → все полномочия
        adminRole?.let { role ->
            val adminAuths = roleJpaRepository.findByIdWithAuthorities(role.id!!)!!.authorities.map { it.name }.toSet()
            listOfNotNull(readAuth, writeAuth, deleteAuth).forEach { auth ->
                if (auth.name !in adminAuths) {
                    role.authorities.add(auth)
                    roleJpaRepository.save(role)
                    log.info("  Linked '${auth.name}' → '${role.name}'")
                }
            }
        }

        // ROLE_USER → только READ
        userRole?.let { role ->
            val userAuths = roleJpaRepository.findByIdWithAuthorities(role.id!!)!!.authorities.map { it.name }.toSet()
            readAuth?.let { auth ->
                if (auth.name !in userAuths) {
                    role.authorities.add(auth)
                    roleJpaRepository.save(role)
                    log.info("  Linked '${auth.name}' → '${role.name}'")
                }
            }
        }
    }
}
