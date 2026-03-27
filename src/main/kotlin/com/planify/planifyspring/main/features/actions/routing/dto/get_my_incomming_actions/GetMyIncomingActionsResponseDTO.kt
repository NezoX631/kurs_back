package com.planify.planifyspring.main.features.actions.routing.dto.get_my_incomming_actions

import com.planify.planifyspring.main.features.actions.routing.dto.ActionDTO

data class GetMyIncomingActionsResponseDTO(
    val actions: List<ActionDTO>
)
