package com.planify.planifyspring.main.features.actions.domain.repositories

import com.planify.planifyspring.main.features.actions.domain.entities.Action

interface ActionsRepository {
    fun createAction(scope: String, type: String, data: Any): Action

    fun deleteAction(scope: String, actionId: String)

    fun getIncomingActions(scope: String, lastSeen: String, count: Long, timeout: Long): List<Action>
}
