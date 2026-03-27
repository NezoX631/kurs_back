package com.planify.planifyspring.main.features.actions.routing.dto

import com.planify.planifyspring.main.features.actions.routing.ActionDTO


data class GetIncomingActionsResponseDTO(
    val actions: List<ActionDTO>
)
