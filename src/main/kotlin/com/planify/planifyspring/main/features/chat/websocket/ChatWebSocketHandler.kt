package com.planify.planifyspring.main.features.chat.websocket

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.ObjectNode
import com.planify.planifyspring.main.features.auth.domain.services.AuthService
import com.planify.planifyspring.main.features.chat.domain.entities.ChatMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatWebSocketHandler(
    private val authService: AuthService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /** Map: sessionId -> userId для отслеживания какой сессии какой пользователь принадлежит */
    private val sessionToUser = ConcurrentHashMap<String, Long>()

    /** Map: userId -> список session (пользователь может быть подключён с нескольких устройств) */
    private val userSessions = ConcurrentHashMap<Long, MutableList<WebSocketSession>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("WebSocket connection established: sessionId=${session.id}, remoteAddress=${session.remoteAddress}")
        // Запускаем периодический ping для поддержания соединения
        startPing(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val payload = message.payload
            val jsonNode = objectMapper.readTree(payload)
            val type = jsonNode.get("type")?.asText()

            when (type) {
                "auth" -> handleAuth(session, jsonNode)
                "pong" -> handlePong(session)
                else -> logger.warn("Unknown message type: $type")
            }
        } catch (e: Exception) {
            logger.error("Error handling WebSocket message from ${session.id}: ${e.message}")
            sendError(session, "Invalid message format")
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = sessionToUser.remove(session.id)
        if (userId != null) {
            userSessions[userId]?.removeIf { it.id == session.id }
            if (userSessions[userId].isNullOrEmpty()) {
                userSessions.remove(userId)
            }
            logger.info("User $userId disconnected from WebSocket")
        }
        logger.info("WebSocket connection closed: sessionId=${session.id}, status=$status")
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("WebSocket transport error for session ${session.id}: ${exception.message}")
        val userId = sessionToUser.remove(session.id)
        if (userId != null) {
            userSessions[userId]?.removeIf { it.id == session.id }
        }
    }

    /** Аутентификация пользователя через WebSocket */
    private fun handleAuth(session: WebSocketSession, jsonNode: JsonNode) {
        val token = jsonNode.get("token")?.asText()
        if (token.isNullOrBlank()) {
            sendError(session, "Token is required")
            session.close(CloseStatus.BAD_DATA)
            return
        }

        try {
            val payload = authService.decodeJwtToken(token)
            val user = authService.getUserById(payload.userId)
            registerSession(user.id, session)
            logger.info("User ${user.id} (${user.username}) authenticated via WebSocket session=${session.id}")

            // Отправляем подтверждение успешной аутентификации
            val response = objectMapper.createObjectNode().apply {
                put("type", "auth_success")
                put("userId", user.id)
                put("timestamp", Instant.now().toString())
            }
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(response)))
        } catch (e: Exception) {
            logger.error("WebSocket auth failed for session ${session.id}: ${e.message}")
            sendError(session, "Authentication failed")
            session.close(CloseStatus.NOT_ACCEPTABLE)
        }
    }

    /** Регистрация сессии пользователя */
    private fun registerSession(userId: Long, session: WebSocketSession) {
        sessionToUser[session.id] = userId
        userSessions.computeIfAbsent(userId) { mutableListOf() }.add(session)
    }

    /** Отправка ping и ожидание pong */
    private fun startPing(session: WebSocketSession) {
        Thread {
            try {
                while (session.isOpen) {
                    Thread.sleep(30_000) // ping каждые 30 секунд
                    if (session.isOpen) {
                        session.sendMessage(PongMessage())
                    }
                }
            } catch (e: Exception) {
                logger.debug("Ping thread interrupted for session ${session.id}")
            }
        }.apply {
            isDaemon = true
            name = "ws-ping-${session.id}"
            start()
        }
    }

    private fun handlePong(session: WebSocketSession) {
        logger.debug("Pong received from session ${session.id}")
    }

    /** Отправить сообщение чата пользователю (все его сессии) */
    fun sendMessageToUser(userId: Long, chatMessage: ChatMessage) {
        val sessions = userSessions[userId]
        if (sessions.isNullOrEmpty()) {
            logger.debug("No active WebSocket sessions for user $userId")
            return
        }

        val json = objectMapper.writeValueAsString(chatMessage)
        val textMessage = TextMessage(json)

        val toRemove = mutableListOf<WebSocketSession>()
        for (session in sessions) {
            try {
                if (session.isOpen) {
                    session.sendMessage(textMessage)
                } else {
                    toRemove.add(session)
                }
            } catch (e: Exception) {
                logger.error("Failed to send WS message to user $userId, session ${session.id}: ${e.message}")
                toRemove.add(session)
            }
        }

        // Удаляем закрытые сессии
        if (toRemove.isNotEmpty()) {
            sessions.removeAll(toRemove.toSet())
            toRemove.forEach { sessionToUser.remove(it.id) }
            if (sessions.isEmpty()) {
                userSessions.remove(userId)
            }
        }
    }

    /** Отправить произвольное JSON-сообщение пользователю (для meeting sync, invites и т.д.) */
    fun sendRawMessage(userId: Long, jsonNode: ObjectNode) {
        val sessions = userSessions[userId]
        if (sessions.isNullOrEmpty()) {
            logger.debug("No active WebSocket sessions for user $userId")
            return
        }

        val json = objectMapper.writeValueAsString(jsonNode)
        val textMessage = TextMessage(json)

        val toRemove = mutableListOf<WebSocketSession>()
        for (session in sessions) {
            try {
                if (session.isOpen) {
                    session.sendMessage(textMessage)
                } else {
                    toRemove.add(session)
                }
            } catch (e: Exception) {
                logger.error("Failed to send raw WS message to user $userId: ${e.message}")
                toRemove.add(session)
            }
        }

        if (toRemove.isNotEmpty()) {
            sessions.removeAll(toRemove.toSet())
            toRemove.forEach { sessionToUser.remove(it.id) }
            if (sessions.isEmpty()) {
                userSessions.remove(userId)
            }
        }
    }

    /** Проверка, есть ли активная WebSocket сессия у пользователя */
    fun hasActiveSession(userId: Long): Boolean {
        val sessions = userSessions[userId]
        return !sessions.isNullOrEmpty() && sessions.any { it.isOpen }
    }

    /** Получить количество подключённых пользователей */
    fun getActiveConnectionsCount(): Int = userSessions.size

    private fun sendError(session: WebSocketSession, errorMessage: String) {
        try {
            val errorNode = objectMapper.createObjectNode().apply {
                put("type", "error")
                put("message", errorMessage)
                put("timestamp", Instant.now().toString())
            }
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(errorNode)))
        } catch (e: Exception) {
            logger.error("Failed to send error message: ${e.message}")
        }
    }
}
