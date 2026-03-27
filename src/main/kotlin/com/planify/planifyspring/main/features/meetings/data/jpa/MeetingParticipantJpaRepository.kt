package com.planify.planifyspring.main.features.meetings.data.jpa

import com.planify.planifyspring.main.features.meetings.data.models.MeetingParticipantModel
import com.planify.planifyspring.main.features.meetings.data.records.DayMeetingsCountRecord
import com.planify.planifyspring.main.features.meetings.data.records.MeetingParticipantIdRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface MeetingParticipantJpaRepository : JpaRepository<MeetingParticipantModel, Long> {
    @Suppress("FunctionName")
    fun existsByUserIdAndMeeting_Id(userId: Long, meetingId: Long): Boolean

    @Query(
        """
            SELECT new com.planify.planifyspring.main.features.meetings.data.records.MeetingParticipantIdRecord(
                m,
                mp.userId
            )
            FROM MeetingParticipantModel mp
            JOIN MeetingModel m
                ON m.id = mp.meeting.id
            WHERE mp.meeting.id = :meetingId
        """
    )
    fun getMeetingWithParticipantIds(meetingId: Long): List<MeetingParticipantIdRecord>

    @Query(
        """
        SELECT new com.planify.planifyspring.main.features.meetings.data.records.MeetingParticipantIdRecord(
            m,
            mp.userId
        )
        FROM MeetingModel m
        JOIN m.participants mp
        WHERE mp.userId = :userId
          AND m.startsAt BETWEEN :startAt AND :endAt
        ORDER BY m.startsAt, m.id
        """
    )
    fun getUserDailyMeetingsWithParticipantIds(userId: Long, startAt: Instant, endAt: Instant): List<MeetingParticipantIdRecord>

    @Query(
        """
            SELECT new com.planify.planifyspring.main.features.meetings.data.records.DayMeetingsCountRecord(
                CAST(m.startsAt AS java.time.LocalDate), 
                COUNT(mp)
            )
            FROM MeetingParticipantModel mp
            JOIN mp.meeting m
            WHERE mp.userId = :userId
                AND m.startsAt BETWEEN :startAt AND :endAt
            GROUP BY CAST(m.startsAt AS java.time.LocalDate)
            ORDER BY CAST(m.startsAt AS java.time.LocalDate)
        """
    )
    fun getUserDailyMeetingsCount(
        userId: Long,
        startAt: Instant,
        endAt: Instant
    ): List<DayMeetingsCountRecord>

    @Query(
        """
            SELECT EXISTS (
                SELECT 1
                FROM meeting_participants mp
                JOIN meetings m
                    ON m.id = mp.meeting_id
                WHERE
                    mp.user_id = :userId AND 
                    m.starts_at BETWEEN :startAt AND :endAt
            ) 
        """, nativeQuery = true
    )
    fun userHasMeetingsBetween(userId: Long, startAt: Instant, endAt: Instant): Boolean
}
