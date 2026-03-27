package com.planify.planifyspring.main.features.meetings.data.repositories_impl

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus
import com.planify.planifyspring.main.features.meetings.domain.repositories.MeetingInvitesRepository
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingInviteParchSchema
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class MeetingInvitesRepositoryImpl(
    // private val redisHelper: RedisHelper
) : MeetingInvitesRepository {

    override fun createInvite(
        meetingId: Long,
        senderId: Long,
        targetId: Long,
        expiresAt: Instant
    ): MeetingInvite {
        val now = Instant.now()
        return MeetingInvite(
            uuid = UUID.randomUUID().toString(),
            meetingId = meetingId,
            senderId = senderId,
            targetId = targetId,
            status = MeetingInviteStatus.PENDING,
            createdAt = now,
            updatedAt = now,
            expiresAt = expiresAt,
            statusData = null
        )
    }

    override fun getInvite(uuid: String): MeetingInvite? {
        println("getInvite called for uuid: $uuid (returning null - Redis disabled)")
        return null
    }

    override fun updateInvite(inviteUuid: String, patch: MeetingInviteParchSchema) {
        println("updateInvite called for uuid: $inviteUuid with patch: $patch (ignored - Redis disabled)")
    }

    override fun getMeetingInvites(meetingId: Long): List<MeetingInvite> {
        println("getMeetingInvites called for meetingId: $meetingId (returning empty list - Redis disabled)")
        return emptyList()
    }
}