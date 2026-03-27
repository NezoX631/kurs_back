package com.planify.planifyspring.main.features.actions.domain.use_cases_impl

import com.planify.planifyspring.core.exceptions.AlreadyInUseAppError
import com.planify.planifyspring.core.exceptions.InvalidArgumentAppError
import com.planify.planifyspring.main.exceptions.generics.AlreadyInUseHttpException
import com.planify.planifyspring.main.features.actions.domain.entities.Action
import com.planify.planifyspring.main.features.actions.domain.exceptions.BadActionIdHttpException
import com.planify.planifyspring.main.features.actions.domain.services.ActionsService
import com.planify.planifyspring.main.features.actions.domain.use_cases.ActionsUseCaseGroup
import org.springframework.stereotype.Component

@Component
class ActionsUseCaseGroupImpl(
    val actionsService: ActionsService
) : ActionsUseCaseGroup {
    override fun createAction(scope: String, type: String, data: Any): Action {
        return actionsService.createAction(scope, type, data)
    }

    override fun createUserAction(userId: Long, type: String, data: Any): Action {
        return actionsService.createUserAction(userId, type, data)
    }

    override fun deleteAction(scope: String, actionId: String) {
        try {
            actionsService.deleteAction(scope, actionId)
        } catch (_: InvalidArgumentAppError) {
            throw BadActionIdHttpException("Invalid actionId specified: $actionId")
        }
    }

    override fun deleteUserAction(userId: Long, actionId: String) {
        try {
            actionsService.deleteUserAction(userId, actionId)
        } catch (_: InvalidArgumentAppError) {
            throw BadActionIdHttpException("Invalid actionId specified: $actionId")
        }
    }

    override fun getIncomingActions(
        scope: String,
        lastSeen: String,
        count: Long,
        timeout: Long
    ): List<Action> {
        try {
            return actionsService.getIncomingActions(scope, lastSeen, count, timeout)
        } catch (_: AlreadyInUseAppError) {
            throw AlreadyInUseHttpException("This customer is already reading actions")
        } catch (_: InvalidArgumentAppError) {
            throw BadActionIdHttpException("Invalid lastSeen action id specified: $lastSeen")
        }
    }

    override fun getUserIncomingActions(userId: Long, lastSeen: String, count: Long, timeout: Long): List<Action> {
        return if (lastSeen.contains("===")) {
            getUserIncomingActionsUsingLastSeenId(userId, lastSeen, count, timeout)
        } else {
            getUserIncomingActionsUsingRecordId(userId, lastSeen, count, timeout)
        }
    }

    override fun getUserIncomingActionsUsingRecordId(userId: Long, recordId: String, count: Long, timeout: Long): List<Action> {
        return try {
            actionsService.getUserIncomingActionsUsingRecordId(userId, recordId, count, timeout)
        } catch (_: AlreadyInUseAppError) {
            throw AlreadyInUseHttpException("This customer is already reading actions")
        } catch (_: InvalidArgumentAppError) {
            throw BadActionIdHttpException("Invalid recordId specified: $recordId")
        }
    }

    override fun getUserIncomingActionsUsingLastSeenId(
        userId: Long,
        actionId: String,
        count: Long,
        timeout: Long
    ): List<Action> {
        return try {
            actionsService.getUserIncomingActionsUsingLastSeenId(userId, actionId, count, timeout)
        } catch (_: AlreadyInUseAppError) {
            throw AlreadyInUseHttpException("This customer is already reading actions")
        } catch (_: InvalidArgumentAppError) {
            throw BadActionIdHttpException("Invalid actionId specified: $actionId")
        }
    }
}
