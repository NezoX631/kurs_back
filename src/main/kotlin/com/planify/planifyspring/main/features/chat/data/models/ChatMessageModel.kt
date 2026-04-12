package com.planify.planifyspring.main.features.chat.data.models

import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "chat_messages", indexes = [
    Index(name = "idx_chat_messages_sender_receiver", columnList = "sender_id, receiver_id"),
    Index(name = "idx_chat_messages_created_at", columnList = "created_at")
])
data class ChatMessageModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val senderId: Long,

    @Column(nullable = false)
    val receiverId: Long,

    @Column(nullable = false, columnDefinition = "TEXT")
    val text: String,

    @Column(nullable = false)
    val isRead: Boolean = false,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
) {
    fun toEntity(): ChatMessage {
        return ChatMessage(
            id = id,
            senderId = senderId,
            receiverId = receiverId,
            text = text,
            isRead = isRead,
            createdAt = createdAt
        )
    }
}
