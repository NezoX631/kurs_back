package com.planify.planifyspring.main.features.chat.routing

import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.common.utils.asSuccessApplicationResponse
import com.planify.planifyspring.main.features.auth.domain.entities.AuthContext
import com.planify.planifyspring.main.features.auth.domain.use_cases.AuthUseCaseGroup
import com.planify.planifyspring.main.features.chat.domain.services.ChatMessagesService
import com.planify.planifyspring.main.features.chat.routing.dto.ChatMessageDTO
import com.planify.planifyspring.main.features.chat.routing.dto.ConversationPreviewDTO
import com.planify.planifyspring.main.features.chat.routing.dto.SendMessageRequestDTO
import com.planify.planifyspring.main.features.chat.websocket.ChatWebSocketHandler
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage

@RestController
@RequestMapping("/chat")
class ChatMessagesController(
    private val chatMessagesService: ChatMessagesService,
    private val authUseCaseGroup: AuthUseCaseGroup,
    private val chatWebSocketHandler: ChatWebSocketHandler
) {
    @PostMapping("/messages")
    fun sendMessage(
        @AuthenticationPrincipal authContext: AuthContext,
        @Valid @RequestBody body: SendMessageRequestDTO
    ): ResponseEntity<ApplicationResponse<ChatMessageDTO>> {
        val message = chatMessagesService.sendMessage(
            senderId = authContext.user.id,
            receiverId = body.receiverId,
            text = body.text
        )

        // Отправляем сообщение через WebSocket получателю в реальном времени
        chatWebSocketHandler.sendMessageToUser(body.receiverId, message)

        // Отправляем сообщение отправителю (чтобы обновить его экран, если он открыт)
        chatWebSocketHandler.sendMessageToUser(authContext.user.id, message)

        // Отправляем push-уведомление получателю
        authUseCaseGroup.sendPushNotification(
            userId = body.receiverId,
            title = authContext.user.username,
            body = body.text,
            type = "chat",
            data = mapOf("chatUserId" to authContext.user.id.toString())
        )

        return ResponseEntity.ok(
            ApplicationResponse(
                ok = true,
                appCode = 1000,
                message = "Success",
                data = ChatMessageDTO.fromEntity(message)
            )
        )
    }

    @GetMapping("/messages/{userId}")
    fun getConversation(
        @AuthenticationPrincipal authContext: AuthContext,
        @PathVariable userId: Long,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 50
    ): ResponseEntity<ApplicationResponse<List<ChatMessageDTO>>> {
        val messages = chatMessagesService.getConversationBetween(
            user1Id = authContext.user.id,
            user2Id = userId,
            pageable = PageRequest.of(page, size)
        )

        // Mark messages as read
        chatMessagesService.markMessagesAsRead(
            senderId = userId,
            receiverId = authContext.user.id
        )

        return ResponseEntity.ok(
            ApplicationResponse(
                ok = true,
                appCode = 1000,
                message = "Success",
                data = messages.content.map { ChatMessageDTO.fromEntity(it) }
            )
        )
    }

    @GetMapping("/conversations")
    fun getConversations(
        @AuthenticationPrincipal authContext: AuthContext
    ): ResponseEntity<ApplicationResponse<List<ConversationPreviewDTO>>> {
        val latestMessages = chatMessagesService.getLatestMessagesPerConversation(authContext.user.id)

        val conversations = latestMessages.map { msg ->
            val otherUserId = if (msg.senderId == authContext.user.id) msg.receiverId else msg.senderId
            val unreadCount = chatMessagesService.getUnreadCountForUser(authContext.user.id)

            ConversationPreviewDTO(
                userId = otherUserId,
                lastMessage = ChatMessageDTO.fromEntity(msg),
                unreadCount = unreadCount
            )
        }

        return ResponseEntity.ok(
            ApplicationResponse(
                ok = true,
                appCode = 1000,
                message = "Success",
                data = conversations
            )
        )
    }

    @GetMapping("/unread/count")
    fun getUnreadCount(
        @AuthenticationPrincipal authContext: AuthContext
    ): ResponseEntity<ApplicationResponse<Map<String, Long>>> {
        val count = chatMessagesService.getUnreadCountForUser(authContext.user.id)

        return ResponseEntity.ok(
            ApplicationResponse(
                ok = true,
                appCode = 1000,
                message = "Success",
                data = mapOf("count" to count)
            )
        )
    }
}
