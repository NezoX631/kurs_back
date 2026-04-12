package com.planify.planifyspring.main.features.actions.data.models

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "actions", indexes = [
    Index(name = "idx_actions_scope", columnList = "scope"),
    Index(name = "idx_actions_scope_record", columnList = "scope,recordId")
])
class ActionModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val uuid: String,

    @Column(nullable = false)
    val recordId: String,

    @Column(nullable = false)
    val scope: String,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val data: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)
