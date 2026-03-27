package com.planify.planifyspring.main.features.actions.data.repositories

import com.planify.planifyspring.core.exceptions.InvalidArgumentAppError
// import com.planify.planifyspring.main.common.utils.redis.RedisHelper
import com.planify.planifyspring.main.features.actions.domain.entities.Action
import com.planify.planifyspring.main.features.actions.domain.repositories.ActionsRepository
// import org.springframework.data.redis.RedisSystemException
// import org.springframework.data.redis.connection.stream.ReadOffset
// import org.springframework.data.redis.connection.stream.RecordId
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ActionsRepositoryImpl(
    // private val redisHelper: RedisHelper
) : ActionsRepository {

    private fun generateActionUuid(): String {
        return UUID.randomUUID().toString()
    }

    private fun getActionScopeStreamKey(scope: String): String {
        return "actions:scope:$scope:stream"
    }

    private fun getActionId(actionUuid: String, recordId: String): String {
        return "${actionUuid}===${recordId}"
    }

    override fun createAction(scope: String, type: String, data: Any): Action {
        // Временно возвращаем заглушку, так как Redis отключен
        val actionUuid = generateActionUuid()
        return Action(
            id = actionUuid,
            type = type,
            data = data.toString()
        )

        /* Оригинальный код с Redis:
        val actionUuid = generateActionUuid()

        val action = Action(
            id = actionUuid,
            type = type,
            data = data.toString()
        )

        val streamKey = getActionScopeStreamKey(scope)
        val recordId = redisHelper.addToStream(streamKey, action)

        return action.copy(
            id = getActionId(actionUuid, recordId.toString())
        )
        */
    }

    override fun deleteAction(scope: String, actionId: String) {
        // Временно ничего не делаем, так как Redis отключен
        println("Delete action called for scope: $scope, actionId: $actionId (ignored - Redis disabled)")

        /* Оригинальный код с Redis:
        val streamKey = getActionScopeStreamKey(scope)

        val idParts = actionId.split("===")
        if (idParts.size != 2) throw InvalidArgumentAppError("Invalid action id: $actionId")

        try {
            redisHelper.deleteFromStream(streamKey, RecordId.of(idParts[1]))
        } catch (e: Exception) {
            throw InvalidArgumentAppError("Failed to delete action: ${e.message}")
        }
        */
    }

    override fun getIncomingActions(
        scope: String,
        lastSeen: String,
        count: Long,
        timeout: Long
    ): List<Action> {
        // Временно возвращаем пустой список, так как Redis отключен
        println("getIncomingActions called for scope: $scope, lastSeen: $lastSeen (ignored - Redis disabled)")
        return emptyList()

        /* Оригинальный код с Redis:
        val streamKey = getActionScopeStreamKey(scope)

        try {
            val results = redisHelper.readStream(
                key = streamKey,
                offset = ReadOffset.from(lastSeen),
                count = count,
                timeout = timeout,
                clazz = Action::class.java
            )

            return results.map { pair ->
                val (recordId, action) = pair
                action.copy(
                    id = getActionId(action.id, recordId.value)
                )
            }
        } catch (error: RedisSystemException) {
            val cause = error.cause
            if (cause?.message?.contains("Invalid stream ID") == true) {
                throw InvalidArgumentAppError("Invalid lastSeen specified")
            }
            throw error
        }
        */
    }
}