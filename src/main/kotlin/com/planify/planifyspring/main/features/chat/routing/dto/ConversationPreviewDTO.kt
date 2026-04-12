package com.planify.planifyspring.main.features.chat.routing.dto

data class ConversationPreviewDTO(
    val userId: Long,
    val lastMessage: ChatMessageDTO,
    val unreadCount: Long
)
