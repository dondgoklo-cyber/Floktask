package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.GoalEntity
import com.taskmanager.domain.model.Goal

fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id ?: 0,
    title = title,
    targetAmount = targetAmount,
    savedAmount = savedAmount,
    currency = currency,
    deadline = deadline,
    createdAt = createdAt
)

fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    title = title,
    targetAmount = targetAmount,
    savedAmount = savedAmount,
    currency = currency,
    deadline = deadline,
    createdAt = createdAt
)
