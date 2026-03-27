package com.planify.planifyspring.main.features.auth.domain.entities

import java.io.Serializable

data class Authority(
    val id: Long,
    val name: String
) : Serializable
