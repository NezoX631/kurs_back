package com.planify.planifyspring.main.features.meetings.data.repositories_impl

import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInvite
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingInviteStatus
import com.planify.planifyspring.main.features.meetings.domain.repositories.MeetingInvitesRepository
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingInviteParchSchema
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.*

@Repository
class MeetingInvitesRepositoryImpl(
    // private val redisHelper: RedisHelper
) : MeetingInvitesRepository {

    // In-memory invites storage (since Redis is disabled)
    private val invites = ConcurrentHashMap<String, MeetingInvite>()

    override fun createInvite(
        meetingId: Long,
        senderId: Long,
        targetId: Long,
        expiresAt: Instant
    ): MeetingInvite {
        val now = Instant.now()
        val invite = MeetingInvite(
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
        invites[invite.uuid] = invite
        return invite
    }

    override fun getInvite(uuid: String): MeetingInvite? {
        return invites[uuid]
    }

    override fun updateInvite(inviteUuid: String, patch: MeetingInviteParchSchema) {
        invites[inviteUuid]?.let { current ->
            val updated = current.copy(
                status = patch.status ?: current.status,
                statusData = patch.statusData ?: current.statusData,
                updatedAt = Instant.now()
            )
            invites[inviteUuid] = updated
        }
    }

    override fun getMeetingInvites(meetingId: Long): List<MeetingInvite> {
        return invites.values.filter { it.meetingId == meetingId }
    }

    override fun getSentInvitesBySender(senderId: Long): List<MeetingInvite> {
        return invites.values.filter { it.senderId == senderId }.sortedByDescending { it.createdAt }
    }
}