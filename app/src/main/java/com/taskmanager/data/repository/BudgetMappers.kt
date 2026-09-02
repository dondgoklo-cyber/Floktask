package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.BudgetEntity
import com.taskmanager.domain.model.Budget
import java.math.BigDecimal

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id ?: 0,
    categoryId = categoryId,
    amount = amount,
    currency = currency
)

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
    currency = currency
)
