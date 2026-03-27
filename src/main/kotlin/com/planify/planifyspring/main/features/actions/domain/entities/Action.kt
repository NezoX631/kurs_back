package com.planify.planifyspring.main.features.actions.domain.entities

data class Action(
    val id: String,
    val type: String,
    val data: Any
)