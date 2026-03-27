package com.planify.planifyspring.main.features.meetings.domain.use_cases_impl

import com.planify.planifyspring.core.exceptions.NotFoundAppError
import com.planify.planifyspring.core.utils.atStartOfAnHour
import com.planify.planifyspring.main.exceptions.generics.BadRequestHttpException
import com.planify.planifyspring.main.exceptions.generics.ForbiddenHttpException
import com.planify.planifyspring.main.exceptions.generics.NotFoundHttpException
import com.planify.planifyspring.main.features.meetings.domain.entities.Meeting
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingParticipant
import com.planify.planifyspring.main.features.meetings.domain.entities.MeetingWithParticipantIds
import com.planify.planifyspring.main.features.meetings.domain.schemas.MeetingPatchSchema
import com.planify.planifyspring.main.features.meetings.domain.services.MeetingsService
import com.planify.planifyspring.main.features.meetings.domain.use_cases.MeetingsServiceUseCaseGroup
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class MeetingsServiceUseCaseGroupImpl(
    val meetingsService: MeetingsService,
) : MeetingsServiceUseCaseGroup {
    override fun createMeeting(
        creatorId: Long,
        name: String,
        description: String,
        location: String,
        startsAt: Instant,
        duration: Int,
    ): Meeting {
        val start = startsAt.atStartOfAnHour()
        val end = start.plusSeconds(duration * 3600L)

        if (start < Instant.now()) throw BadRequestHttpException("Cannot create meeting in the past")

        if (meetingsService.userHasMeetingsBetween(userId = creatorId, startAt = start, endAt = end)) throw BadRequestHttpException("User already has meeting at this time interval")
        return meetingsService.createMeeting(creatorId, name, description, location, startsAt, duration)
    }

    override fun createMeetingParticipant(
        meetingId: Long,
        userId: Long
    ): MeetingParticipant {
        return meetingsService.createMeetingParticipant(meetingId, userId)
    }

    override fun isUserParticipant(userId: Long, meetingId: Long): Boolean {
        return meetingsService.isUserParticipant(userId, meetingId)
    }

    @Transactional
    override fun rescheduleMeeting(meetingId: Long, rescheduleTo: Instant, requesterId: Long) {
        patchMeeting(
            meetingId = meetingId,
            requesterId = requesterId,
            patch = MeetingPatchSchema(
                startsAt = rescheduleTo
            )
        )
    }

    override fun getMeetingById(
        meetingId: Long,
        requesterId: Long
    ): Meeting {
//        if (!isUserParticipant(requesterId, meetingId)) throw BadRequestHttpException("Cannot get meeting info: user is not participant of this meeting")

        try {
            return meetingsService.getMeetingById(meetingId)
        } catch (_: NotFoundAppError) {
            throw NotFoundHttpException("Meeting was not found")
        }
    }

    override fun getMeetingWithParticipantIds(
        meetingId: Long,
        requesterId: Long
    ): MeetingWithParticipantIds {
//        if (!isUserParticipant(requesterId, meetingId)) throw BadRequestHttpException("Cannot get meeting info: user is not participant of this meeting")
        try {
            return meetingsService.getMeetingWithParticipantIds(meetingId)
        } catch (_: NotFoundAppError) {
            throw NotFoundHttpException("Meeting was not found")
        }
    }

    @Transactional
    override fun patchMeeting(
        meetingId: Long,
        patch: MeetingPatchSchema,
        requesterId: Long
    ) {
        val meeting: Meeting

        try {
            meeting = meetingsService.getMeetingById(meetingId)  // TODO: Optimize it via db request
        } catch (_: NotFoundAppError) {
            throw NotFoundHttpException("Cannot patch meeting: Meeting was not found")
        }

        if (meeting.ownerId != requesterId) throw ForbiddenHttpException("Cannot patch meeting: you are not the owner")
        if (meeting.startsAt < Instant.now()) throw BadRequestHttpException("Cannot reschedule meeting to the past")

        return meetingsService.patchMeeting(meetingId, patch)
    }

    override fun getUserDailyMeetingsWithParticipantIds(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): Map<Instant, List<MeetingWithParticipantIds>> {
        return meetingsService.getUserDailyMeetingsWithParticipantIds(userId, startAt, endAt)
    }

    override fun getUserDailyMeetingsShort(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): Map<Instant, Long> {
        return meetingsService.getUserDailyMeetingsShort(userId, startAt, endAt)
    }
}
