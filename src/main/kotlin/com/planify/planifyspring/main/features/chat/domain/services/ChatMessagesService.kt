package com.planify.planifyspring.main.features.chat.domain.services

import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ChatMessagesService {
    fun sendMessage(senderId: Long, receiverId: Long, text: String): ChatMessage
    fun getConversationBetween(user1Id: Long, user2Id: Long, pageable: Pageable): Page<ChatMessage>
    fun getUnreadCountForUser(userId: Long): Long
    fun markMessagesAsRead(senderId: Long, receiverId: Long): Int
    fun getLatestMessagesPerConversation(userId: Long): List<ChatMessage>
}
