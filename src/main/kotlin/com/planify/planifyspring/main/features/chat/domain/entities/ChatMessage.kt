package com.planify.planifyspring.main.features.chat.domain.entities

import java.time.Instant

data class ChatMessage(
    val id: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val isRead: Boolean = false,
    val createdAt: Instant = Instant.now()
)
