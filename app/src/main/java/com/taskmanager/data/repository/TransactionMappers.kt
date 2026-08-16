package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.TransactionEntity
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import java.time.Instant

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id ?: 0,
    amount = amount,
    type = type.name,
    currency = currency,
    categoryId = categoryId,
    accountId = accountId,
    date = date.toEpochMilli(),
    note = note,
    toAccountId = toAccountId,
    destinationAmount = destinationAmount,
    destinationCurrency = destinationCurrency,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    amount = amount,
    type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
    currency = currency,
    categoryId = categoryId,
    accountId = accountId,
    date = Instant.ofEpochMilli(date),
    note = note,
    toAccountId = toAccountId,
    destinationAmount = destinationAmount,
    destinationCurrency = destinationCurrency,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)
