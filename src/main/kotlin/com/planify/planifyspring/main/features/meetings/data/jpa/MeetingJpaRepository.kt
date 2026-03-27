package com.planify.planifyspring.main.features.meetings.data.jpa

import com.planify.planifyspring.main.features.meetings.data.models.MeetingModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface MeetingJpaRepository : JpaRepository<MeetingModel, Long> {
    @Modifying
    @Query(
        """
            UPDATE MeetingModel m
            SET 
                m.name = COALESCE(:name, m.name),
                m.startsAt = COALESCE(:startsAt, m.startsAt),
                m.duration = COALESCE(:duration, m.duration),
                m.description = COALESCE(:description, m.description),
                m.location = COALESCE(:location, m.location)
            WHERE m.id = :meetingId
        """
    )
    fun parchMeeting(
        meetingId: Long,
        name: String?,
        startsAt: Instant?,
        duration: Int?,
        description: String?,
        location: String?
    )
}
