package com.planify.planifyspring.main.features.actions.data.repositories

import com.planify.planifyspring.core.exceptions.InvalidArgumentAppError
import com.planify.planifyspring.main.features.actions.data.jpa.ActionJpaRepository
import com.planify.planifyspring.main.features.actions.data.models.ActionModel
import com.planify.planifyspring.main.features.actions.domain.entities.Action
import com.planify.planifyspring.main.features.actions.domain.repositories.ActionsRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class ActionsRepositoryImpl(
    private val actionJpaRepository: ActionJpaRepository
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
        val actionUuid = generateActionUuid()
        val recordId = UUID.randomUUID().toString()
        val now = Instant.now()

        val model = ActionModel(
            uuid = actionUuid,
            recordId = recordId,
            scope = scope,
            type = type,
            data = objectMapper.writeValueAsString(data),
            createdAt = now
        )

        actionJpaRepository.save(model)

        return Action(
            id = getActionId(actionUuid, recordId),
            type = type,
            data = data.toString(),
            createdAt = now
        )
    }

    override fun deleteAction(scope: String, actionId: String) {
        val idParts = actionId.split("===")
        if (idParts.size != 2) throw InvalidArgumentAppError("Invalid action id: $actionId")

        val actionUuid = idParts[0]
        actionJpaRepository.findByUuid(actionUuid)?.let {
            actionJpaRepository.delete(it)
        }
    }

    override fun deleteUserActionByInviteUuid(userId: Long, inviteUuid: String, type: String): Boolean {
        val scope = "users:$userId"
        val actions = actionJpaRepository.findByScopeAndTypeAndInviteUuid(scope, type, inviteUuid)
        if (actions.isNotEmpty()) {
            actionJpaRepository.deleteAll(actions)
            return true
        }
        return false
    }

    override fun getIncomingActions(
        scope: String,
        lastSeen: String,
        count: Long,
        timeout: Long
    ): List<Action> {
        // lastSeen имеет формат "recordId" или "uuid===recordId"
        val recordId = if (lastSeen.contains("===")) {
            lastSeen.split("===")[1]
        } else {
            lastSeen
        }

        // Если lastSeen пустой или "0-0", возвращаем все actions для scope
        val actions = if (recordId.isEmpty() || recordId == "0-0") {
            actionJpaRepository.findByScopeOrderByCreatedAtAsc(scope)
        } else {
            // Возвращаем actions после указанного recordId
            actionJpaRepository.findByScopeAndRecordIdGreaterThanOrderByCreatedAtAsc(scope, recordId)
        }

        return actions.take(count.toInt()).map { model ->
            // Десериализуем data из JSON строки обратно в Map
            val dataObj = try {
                objectMapper.readValue(model.data, Any::class.java)
            } catch (e: Exception) {
                model.data
            }

            Action(
                id = getActionId(model.uuid, model.recordId),
                type = model.type,
                data = dataObj,
                createdAt = model.createdAt
            )
        }
    }

    companion object {
        private val objectMapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
            .registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    }
}
