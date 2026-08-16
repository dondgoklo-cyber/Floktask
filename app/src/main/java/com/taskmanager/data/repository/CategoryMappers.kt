package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.CategoryEntity
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id ?: 0,
    name = name,
    type = type.name,
    color = color,
    icon = icon,
    isDefault = isDefault
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    type = runCatching { CategoryType.valueOf(type) }.getOrDefault(CategoryType.EXPENSE),
    color = color,
    icon = icon,
    isDefault = isDefault
)
