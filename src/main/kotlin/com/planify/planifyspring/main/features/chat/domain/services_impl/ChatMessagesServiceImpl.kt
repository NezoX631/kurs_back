package com.planify.planifyspring.main.features.chat.domain.services_impl

import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage
import com.planify.planifyspring.main.features.chat.domain.repositories.ChatMessagesRepository
import com.planify.planifyspring.main.features.chat.domain.services.ChatMessagesService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessagesServiceImpl(
    private val chatMessagesRepository: ChatMessagesRepository
) : ChatMessagesService {

    @Transactional
    override fun sendMessage(senderId: Long, receiverId: Long, text: String): ChatMessage {
        return chatMessagesRepository.sendMessage(senderId, receiverId, text)
    }

    @Transactional(readOnly = true)
    override fun getConversationBetween(user1Id: Long, user2Id: Long, pageable: Pageable): Page<ChatMessage> {
        return chatMessagesRepository.getConversationBetween(user1Id, user2Id, pageable)
    }

    @Transactional(readOnly = true)
    override fun getUnreadCountForUser(userId: Long): Long {
        return chatMessagesRepository.getUnreadCountForUser(userId)
    }

    @Transactional
    override fun markMessagesAsRead(senderId: Long, receiverId: Long): Int {
        return chatMessagesRepository.markMessagesAsRead(senderId, receiverId)
    }

    @Transactional(readOnly = true)
    override fun getLatestMessagesPerConversation(userId: Long): List<ChatMessage> {
        return chatMessagesRepository.getLatestMessagesPerConversation(userId)
    }
}
