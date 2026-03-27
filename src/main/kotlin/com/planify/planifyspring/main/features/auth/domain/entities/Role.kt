package com.planify.planifyspring.main.features.auth.domain.entities

import java.io.Serializable

data class Role(
    val id: Long,
    val name: String
) : Serializable
