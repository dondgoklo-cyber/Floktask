package com.taskmanager.domain.model

import java.time.Instant

/**
 * A file attached to a task (issue 37: tasks were text-only; need
 * images/PDFs/audio notes with preview). Stores a content URI + metadata.
 */
data class Attachment(
    val id: Long? = null,
    val taskId: Long,
    val name: String,
    val mimeType: String,
    val uri: String,
    val sizeBytes: Long,
    val createdAt: Instant = Instant.now()
) {
    val type: AttachmentType get() = AttachmentType.fromMime(mimeType)
}

enum class AttachmentType(val variances: Set<String>) {
    IMAGE(setOf("image/jpeg", "image/png", "image/webp")),
    PDF(setOf("application/pdf")),
    AUDIO(setOf("audio/mpeg", "audio/mp4", "audio/3gpp", "audio/ogg")),
    VIDEO(setOf("video/mp4", "video/3gpp")),
    OTHER(emptySet());

    companion object {
        fun fromMime(mime: String): AttachmentType =
            entries.firstOrNull { it.variances.any { v -> mime.equals(v, ignoreCase = true) } }
                ?: OTHER
    }
}
