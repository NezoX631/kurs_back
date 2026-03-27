package com.planify.planifyspring.main.features.meetings.data.repositories_impl

import com.planify.planifyspring.core.utils.atStartOfDay
import com.planify.planifyspring.core.utils.atStartOfDayInstant
import com.planify.planifyspring.main.features.meetings.data.jpa.MeetingJpaRepository
import com.planify.planifyspring.main.features.meetings.data.jpa.MeetingParticipantJpaRepository
import com.planify.planifyspring.main.features.meetings.data.models.MeetingModel
import com.planify.planifyspring.main.features.meetings.data.models.MeetingParticipantModel
import com.planify.planifyspring.main.features.meetings.domain.entities.Meeting
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingParticipant
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingWithParticipantIds
import com.planify.planifyspring.main.features.meetings.domain.repositories.MeetingsRepository
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingPatchSchema
import jakarta.persistence.EntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class MeetingsRepositoryImpl(
    val meetingsJpaRepository: MeetingJpaRepository,
    val meetingParticipantJpaRepository: MeetingParticipantJpaRepository,
    val entityManager: EntityManager
) : MeetingsRepository {
    override fun createMeeting(
        ownerId: Long,
        name: String,
        description: String,
        location: String,
        startsAt: Instant,
        duration: Int
    ): Meeting {
        val model = MeetingModel(
            ownerId = ownerId,
            name = name,
            description = description,
            location = location,
            startsAt = startsAt,
            duration = duration
        )

        meetingsJpaRepository.save(model)
        return model.toEntity()
    }

    override fun getMeetingById(meetingId: Long): Meeting? {
        return meetingsJpaRepository.findByIdOrNull(meetingId)?.toEntity()
    }

    override fun patchMeeting(meetingId: Long, patch: MeetingPatchSchema) {
        meetingsJpaRepository.parchMeeting(
            meetingId = meetingId,
            name = patch.name,
            description = patch.description,
            location = patch.location,
            startsAt = patch.startsAt,
            duration = patch.duration
        )
    }

    override fun getUserDailyMeetingsWithParticipantIds(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): Map<Instant, List<MeetingWithParticipantIds>> {
        val records = meetingParticipantJpaRepository.getUserDailyMeetingsWithParticipantIds(userId, startAt, endAt)

        return records
            .groupBy { it.meeting.startsAt.atStartOfDay() }
            .mapValues { (_, value) ->
                value.groupBy { it.meeting }
                    .map { (meeting, v) ->
                        MeetingWithParticipantIds(
                            meeting = meeting.toEntity(),
                            participantIds = v.map { it.participantId }
                        )
                    }
            }
    }

    override fun getUserDailyMeetingsShort(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): Map<Instant, Long> {
        val records = meetingParticipantJpaRepository.getUserDailyMeetingsCount(userId, startAt, endAt)
        return records.associate { it.date.atStartOfDayInstant() to it.count }
    }

    override fun createMeetingParticipant(meetingId: Long, userId: Long): MeetingParticipant {
        val meetingRef = entityManager.getReference(MeetingModel::class.java, meetingId)

        val model = MeetingParticipantModel(
            meeting = meetingRef,
            userId = userId
        )

        meetingParticipantJpaRepository.save(model)

        return model.toEntity()
    }

    override fun getMeetingWithParticipantIds(meetingId: Long): MeetingWithParticipantIds? {
        val records = meetingParticipantJpaRepository.getMeetingWithParticipantIds(meetingId)
        if (records.isEmpty()) return null

        return MeetingWithParticipantIds(
            meeting = records[0].meeting.toEntity(),
            participantIds = records.map { it.participantId }
        )
    }

    override fun isUserParticipant(userId: Long, meetingId: Long): Boolean {
        return meetingParticipantJpaRepository.existsByUserIdAndMeeting_Id(userId, meetingId)
    }

    override fun userHasMeetingsBetween(userId: Long, startAt: Instant, endAt: Instant): Boolean {
        return meetingParticipantJpaRepository.userHasMeetingsBetween(userId, startAt, endAt)
    }
}
