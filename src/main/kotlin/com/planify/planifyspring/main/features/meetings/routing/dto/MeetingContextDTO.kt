package com.planify.planifyspring.main.features.meetings.routing.dto

import com.planify.planifyspring.main.features.profiles.routing.dto.ProfileDTO

data class MeetingContextDTO(
    val participantProfiles: List<ProfileDTO>,
    val invites: List<MeetingInviteDTO>,
    val meeting: MeetingDTO,
    val invitedUserProfiles: List<ProfileDTO>
)
