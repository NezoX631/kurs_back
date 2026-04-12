package com.planify.planifyspring.main.features.actions.domain.entities

import java.time.Instant

data class Action(
    val id: String,
    val type: String,
    val data: Any,
    val createdAt: Instant = Instant.now()
)