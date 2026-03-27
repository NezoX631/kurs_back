package com.planify.planifyspring.main.features.profiles.domain.services_impl

import com.planify.planifyspring.main.common.utils.JsonCacheWrapper
import com.planify.planifyspring.main.features.profiles.domain.entiries.Profile
import com.planify.planifyspring.main.features.profiles.domain.repositories.ProfilesRepository
import com.planify.planifyspring.main.features.profiles.domain.schemas.CreateProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.schemas.PatchProfileSchema
import com.planify.planifyspring.main.features.profiles.domain.services.ProfilesService
import org.springframework.cache.CacheManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class ProfilesServiceImpl(
    private val profilesRepository: ProfilesRepository,
    private val cacheManager: CacheManager,
    private val objectMapper: ObjectMapper
) : ProfilesService {
    override fun getProfileById(userId: Long): Profile? {
        val cache = JsonCacheWrapper(cacheManager.getCache("profiles:$userId")!!, objectMapper)
        val cached = cache.getAs<Profile>("profiles:$userId")
        if (cached != null) return cached

        return profilesRepository.getProfileById(userId)
            .also { profile -> profile?.let { cache.put("profiles:$userId", profile) } }
    }

    override fun patchProfile(userId: Long, patch: PatchProfileSchema) {
        val cache = cacheManager.getCache("profiles:${userId}")!!
        cache.evict("profiles:${userId}")

        return profilesRepository.patchProfile(userId, patch)
    }

    override fun search(
        input: String,
        pageable: Pageable
    ): Page<Profile> {
        return profilesRepository.search(input, pageable)
    }

    override fun createProfile(
        userId: Long,
        schema: CreateProfileSchema
    ): Profile {
        return profilesRepository.createProfile(userId, schema)
    }
}
