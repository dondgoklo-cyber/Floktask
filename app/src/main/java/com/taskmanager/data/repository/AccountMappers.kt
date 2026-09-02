package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.AccountEntity
import com.taskmanager.domain.model.Account
import java.math.BigDecimal

fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id ?: 0,
    name = name,
    initialBalance = initialBalance,
    currency = currency
)

fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    initialBalance = initialBalance,
    currency = currency
)
