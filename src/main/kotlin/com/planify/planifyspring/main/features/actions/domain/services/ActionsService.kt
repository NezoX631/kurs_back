package com.planify.planifyspring.main.features.actions.domain.services

import com.planify.planifyspring.main.features.actions.domain.entities.Action

interface ActionsService {
    fun getUserActionsScope(userId: Long): String

    fun createAction(scope: String, type: String, data: Any): Action
    fun createUserAction(userId: Long, type: String, data: Any): Action

    fun deleteAction(scope: String, actionId: String)
    fun deleteUserAction(userId: Long, actionId: String)

    fun getIncomingActions(scope: String, lastSeen: String, count: Long, timeout: Long): List<Action>
    fun getUserIncomingActionsUsingRecordId(userId: Long, lastSeen: String, count: Long, timeout: Long): List<Action>
    fun getUserIncomingActionsUsingLastSeenId(userId: Long, actionId: String, count: Long, timeout: Long): List<Action>
}
