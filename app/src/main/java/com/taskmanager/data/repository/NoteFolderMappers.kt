package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.NoteFolderEntity
import com.taskmanager.domain.model.NoteFolder

fun NoteFolder.toEntity(): NoteFolderEntity = NoteFolderEntity(
    id = id ?: 0,
    name = name
)

fun NoteFolderEntity.toDomain(): NoteFolder = NoteFolder(
    id = id,
    name = name
)
