package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.TagEntity
import com.taskmanager.domain.model.Tag

fun Tag.toEntity(): TagEntity = TagEntity(
    id = id ?: 0,
    name = name,
    color = color
)

fun TagEntity.toDomain(): Tag = Tag(
    id = id,
    name = name,
    color = color
)
