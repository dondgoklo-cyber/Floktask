package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.LocationReminderEntity
import com.taskmanager.domain.model.LocationReminder
import java.time.Instant

fun LocationReminderEntity.toDomain(): LocationReminder = LocationReminder(
    id = id,
    taskId = taskId,
    label = label,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
    isActive = isActive,
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun LocationReminder.toEntity(): LocationReminderEntity = LocationReminderEntity(
    id = id ?: 0,
    taskId = taskId,
    label = label,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
    isActive = isActive,
    createdAt = createdAt.toEpochMilli()
)
