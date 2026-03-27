package com.planify.planifyspring.main.features.meetings.domain.use_cases

import com.planify.planifyspring.main.features.meetings.domain.entities.Meeting
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingParticipant
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingWithParticipantIds
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingPatchSchema
import java.time.Instant

interface MeetingsServiceUseCaseGroup {
    fun createMeeting(
        creatorId: Long,
        name: String,
        description: String,
        location: String,
        startsAt: Instant,
        duration: Int
    ): Meeting

    fun getMeetingById(
        meetingId: Long,
        requesterId: Long
    ): Meeting?

    fun patchMeeting(
        meetingId: Long,
        patch: MeetingPatchSchema,
        requesterId: Long
    )

    fun getUserDailyMeetingsWithParticipantIds(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): Map<Instant, List<MeetingWithParticipantIds>>

    fun getUserDailyMeetingsShort(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): Map<Instant, Long>

    fun createMeetingParticipant(
        meetingId: Long,
        userId: Long
    ): MeetingParticipant

    fun isUserParticipant(
        userId: Long,
        meetingId: Long,
    ): Boolean

    fun rescheduleMeeting(
        meetingId: Long,
        rescheduleTo: Instant,
        requesterId: Long
    )

    fun getMeetingWithParticipantIds(
        meetingId: Long,
        requesterId: Long
    ): MeetingWithParticipantIds
}
