package com.planify.planifyspring.main.features.chat.routing.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage

data class ChatMessageDTO(
    val id: Long,
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    @JsonProperty("isRead")
    val isRead: Boolean,
    val createdAt: String
) {
    companion object {
        fun fromEntity(entity: ChatMessage): ChatMessageDTO {
            return ChatMessageDTO(
                id = entity.id,
                senderId = entity.senderId,
                receiverId = entity.receiverId,
                text = entity.text,
                isRead = entity.isRead,
                createdAt = entity.createdAt.toString()
            )
        }
    }
}
