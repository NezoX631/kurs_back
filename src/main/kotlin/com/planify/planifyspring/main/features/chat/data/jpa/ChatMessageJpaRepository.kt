package com.planify.planifyspring.main.features.chat.data.jpa

import com.planify.planifyspring.main.features.chat.data.models.ChatMessageModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageJpaRepository : JpaRepository<ChatMessageModel, Long> {

    @Query("""
        SELECT m FROM ChatMessageModel m 
        WHERE (m.senderId = :user1Id AND m.receiverId = :user2Id) 
           OR (m.senderId = :user2Id AND m.receiverId = :user1Id)
        ORDER BY m.createdAt DESC
    """)
    fun findConversationBetween(@Param("user1Id") user1Id: Long, @Param("user2Id") user2Id: Long, pageable: Pageable): Page<ChatMessageModel>

    @Query("""
        SELECT COUNT(m) FROM ChatMessageModel m
        WHERE m.receiverId = :userId AND m.isRead = false
    """)
    fun countUnreadMessagesForUser(@Param("userId") userId: Long): Long

    @Modifying
    @Query("""
        UPDATE ChatMessageModel m SET m.isRead = true 
        WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.isRead = false
    """)
    fun markMessagesAsRead(@Param("senderId") senderId: Long, @Param("receiverId") receiverId: Long): Int

    @Query("""
        SELECT m FROM ChatMessageModel m
        WHERE m.id IN (
            SELECT MAX(m2.id) FROM ChatMessageModel m2
            WHERE (m2.senderId = :userId OR m2.receiverId = :userId)
            GROUP BY 
                CASE WHEN m2.senderId = :userId THEN m2.receiverId ELSE m2.senderId END
        )
        ORDER BY m.createdAt DESC
    """)
    fun findLatestMessagesPerConversation(@Param("userId") userId: Long): List<ChatMessageModel>
}
