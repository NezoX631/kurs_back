package com.planify.planifyspring.main.features.meetings.data.models

import com.planify.planifyspring.main.features.meetings.domain.entities.Meeting
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "meetings")
open class MeetingModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    @Column(nullable = false)
    open val ownerId: Long,

    @Column(nullable = false)
    open val name: String,

    @Column(nullable = false)
    open val startsAt: Instant,

    @Column(nullable = false)
    open val duration: Int,

    @Column(nullable = true)
    open val description: String,

    @Column(nullable = true)
    open val location: String,

    @OneToMany(
        mappedBy = "meeting",
        fetch = FetchType.LAZY,
        cascade = [],
        orphanRemoval = false
    )
    open val participants: MutableSet<MeetingParticipantModel> = mutableSetOf()
) {
    fun toEntity(): Meeting = Meeting(
        id = id!!,
        ownerId = ownerId,
        name = name,
        startsAt = startsAt,
        duration = duration,
        description = description,
        location = location
    )
}
