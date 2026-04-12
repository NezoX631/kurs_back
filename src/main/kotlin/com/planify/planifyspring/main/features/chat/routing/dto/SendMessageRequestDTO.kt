package com.planify.planifyspring.main.features.chat.routing.dto

import jakarta.validation.constraints.NotBlank

data class SendMessageRequestDTO(
    val receiverId: Long,
    @field:NotBlank(message = "Message text cannot be empty")
    val text: String
)
