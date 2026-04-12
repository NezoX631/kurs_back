package com.planify.planifyspring.main.features.chat.data.repositories_impl

import com.planify.planifyspring.main.features.chat.data.jpa.ChatMessageJpaRepository
import com.planify.planifyspring.main.features.chat.data.models.ChatMessageModel
import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage
import com.planify.planifyspring.main.features.chat.domain.repositories.ChatMessagesRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ChatMessagesRepositoryImpl(
    private val chatMessageJpaRepository: ChatMessageJpaRepository
) : ChatMessagesRepository {

    override fun sendMessage(senderId: Long, receiverId: Long, text: String): ChatMessage {
        val model = ChatMessageModel(
            senderId = senderId,
            receiverId = receiverId,
            text = text
        )
        val saved = chatMessageJpaRepository.save(model)
        return saved.toEntity()
    }

    override fun getConversationBetween(user1Id: Long, user2Id: Long, pageable: Pageable): Page<ChatMessage> {
        return chatMessageJpaRepository.findConversationBetween(user1Id, user2Id, pageable)
            .map { it.toEntity() }
    }

    override fun getUnreadCountForUser(userId: Long): Long {
        return chatMessageJpaRepository.countUnreadMessagesForUser(userId)
    }

    @Transactional
    override fun markMessagesAsRead(senderId: Long, receiverId: Long): Int {
        return chatMessageJpaRepository.markMessagesAsRead(senderId, receiverId)
    }

    override fun getLatestMessagesPerConversation(userId: Long): List<ChatMessage> {
        return chatMessageJpaRepository.findLatestMessagesPerConversation(userId)
            .map { it.toEntity() }
    }
}
