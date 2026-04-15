package com.planify.planifyspring.main.features.sync

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import com.planify.planifyspring.main.features.chat.websocket.ChatWebSocketHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Сервис для отправки WebSocket-уведомлений о событиях в приложении.
 * Используется для синхронизации данных между клиентами в реальном времени.
 */
@Service
class WebSocketSyncService(
    private val chatWebSocketHandler: ChatWebSocketHandler,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    // ===== MEETING EVENTS =====

    /** Встреча создана — уведомляем всех участников */
    fun notifyMeetingCreated(meetingId: Long, ownerId: Long, participantIds: List<Long>, meetingName: String) {
        val allUserIds = listOf(ownerId) + participantIds
        allUserIds.forEach { userId ->
            sendMeetingEvent(userId, "meeting_created", objectMapper.createObjectNode().apply {
                put("meetingId", meetingId)
                put("ownerId", ownerId)
                put("meetingName", meetingName)
                put("timestamp", Instant.now().toString())
            })
        }
        logger.info("WS sync: meeting_created sent to ${allUserIds.size} users for meeting=$meetingId")
    }

    /** Встреча обновлена — уведомляем участников */
    fun notifyMeetingUpdated(meetingId: Long, participantIds: List<Long>, meetingName: String) {
        participantIds.forEach { userId ->
            sendMeetingEvent(userId, "meeting_updated", objectMapper.createObjectNode().apply {
                put("meetingId", meetingId)
                put("meetingName", meetingName)
                put("timestamp", Instant.now().toString())
            })
        }
        logger.info("WS sync: meeting_updated sent to ${participantIds.size} users for meeting=$meetingId")
    }

    /** Встреча удалена — уведомляем участников */
    fun notifyMeetingDeleted(meetingId: Long, participantIds: List<Long>) {
        participantIds.forEach { userId ->
            sendMeetingEvent(userId, "meeting_deleted", objectMapper.createObjectNode().apply {
                put("meetingId", meetingId)
                put("timestamp", Instant.now().toString())
            })
        }
        logger.info("WS sync: meeting_deleted sent to ${participantIds.size} users for meeting=$meetingId")
    }

    // ===== INVITE EVENTS =====

    /** Приглашение отправлено — уведомляем приглашённого */
    fun notifyInviteSent(targetId: Long, senderId: Long, senderName: String, meetingId: Long, meetingName: String, inviteUuid: String) {
        sendMeetingEvent(targetId, "invite_received", objectMapper.createObjectNode().apply {
            put("inviteUuid", inviteUuid)
            put("meetingId", meetingId)
            put("meetingName", meetingName)
            put("senderId", senderId)
            put("senderName", senderName)
            put("timestamp", Instant.now().toString())
        })
        logger.info("WS sync: invite_received sent to user=$targetId for meeting=$meetingId")
    }

    /** Приглашение принято — уведомляем организатора */
    fun notifyInviteAccepted(inviteUuid: String, meetingId: Long, targetId: Long, targetName: String, ownerId: Long) {
        sendMeetingEvent(ownerId, "invite_accepted", objectMapper.createObjectNode().apply {
            put("inviteUuid", inviteUuid)
            put("meetingId", meetingId)
            put("targetId", targetId)
            put("targetName", targetName)
            put("timestamp", Instant.now().toString())
        })
        logger.info("WS sync: invite_accepted sent to user=$ownerId for invite=$inviteUuid")
    }

    /** Приглашение отклонено — уведомляем организатора */
    fun notifyInviteRejected(inviteUuid: String, meetingId: Long, targetId: Long, targetName: String, ownerId: Long) {
        sendMeetingEvent(ownerId, "invite_rejected", objectMapper.createObjectNode().apply {
            put("inviteUuid", inviteUuid)
            put("meetingId", meetingId)
            put("targetId", targetId)
            put("targetName", targetName)
            put("timestamp", Instant.now().toString())
        })
        logger.info("WS sync: invite_rejected sent to user=$ownerId for invite=$inviteUuid")
    }

    /** Запрос на перенос встречи */
    fun notifyRescheduleRequested(inviteUuid: String, meetingId: Long, requesterId: Long, requesterName: String, ownerId: Long, newTime: String) {
        sendMeetingEvent(ownerId, "reschedule_requested", objectMapper.createObjectNode().apply {
            put("inviteUuid", inviteUuid)
            put("meetingId", meetingId)
            put("requesterId", requesterId)
            put("requesterName", requesterName)
            put("newTime", newTime)
            put("timestamp", Instant.now().toString())
        })
        logger.info("WS sync: reschedule_requested sent to user=$ownerId for invite=$inviteUuid")
    }

    /** Ответ на запрос переноса */
    fun notifyRescheduleAnswered(inviteUuid: String, meetingId: Long, responderId: Long, responderName: String, requesterId: Long, accepted: Boolean) {
        sendMeetingEvent(requesterId, "reschedule_answered", objectMapper.createObjectNode().apply {
            put("inviteUuid", inviteUuid)
            put("meetingId", meetingId)
            put("responderId", responderId)
            put("responderName", responderName)
            put("accepted", accepted)
            put("timestamp", Instant.now().toString())
        })
        logger.info("WS sync: reschedule_answered sent to user=$requesterId for invite=$inviteUuid")
    }

    // ===== ACTION / NOTIFICATION EVENTS =====

    /** Новое действие (action) доступно — long-polling альтернатива */
    fun notifyNewAction(userId: Long, actionType: String, actionId: String?) {
        sendMeetingEvent(userId, "new_action", objectMapper.createObjectNode().apply {
            put("actionType", actionType)
            put("actionId", actionId)
            put("timestamp", Instant.now().toString())
        })
        logger.info("WS sync: new_action sent to user=$userId, type=$actionType")
    }

    // ===== CHAT EVENTS (дополнительно к sendMessageToUser) =====

    /** Новое сообщение (альтернативный метод через raw message) */
    fun notifyNewMessage(userId: Long, senderId: Long, text: String, messageId: Long) {
        sendMeetingEvent(userId, "new_message", objectMapper.createObjectNode().apply {
            put("messageId", messageId)
            put("senderId", senderId)
            put("text", text)
            put("timestamp", Instant.now().toString())
        })
    }

    /** Счётчик непрочитанных обновлён */
    fun notifyUnreadCountUpdated(userId: Long, unreadCount: Int) {
        sendMeetingEvent(userId, "unread_count_updated", objectMapper.createObjectNode().apply {
            put("unreadCount", unreadCount)
            put("timestamp", Instant.now().toString())
        })
    }

    // ===== INTERNAL HELPERS =====

    private fun sendMeetingEvent(userId: Long, eventType: String, data: ObjectNode) {
        val message = objectMapper.createObjectNode().apply {
            put("type", eventType)
            set("data", data)
        }
        chatWebSocketHandler.sendRawMessage(userId, message)
    }
}
