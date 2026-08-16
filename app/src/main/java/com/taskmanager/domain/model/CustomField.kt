package com.taskmanager.domain.model

import java.time.Instant

/**
 * A user-defined field attached to a task (issue 39: all tasks had the same
 * structure; need custom fields for CRM/inventory/etc.).
 */
data class CustomField(
    val id: Long? = null,
    val name: String,
    val type: CustomFieldType,
    val options: List<String> = emptyList() // for DROPDOWN
)

enum class CustomFieldType { TEXT, NUMBER, DROPDOWN, DATE }

/**
 * A value assigned to a custom field on a task. Stored as a string and
 * parsed/validated per the field type.
 */
data class CustomFieldValue(
    val fieldId: Long,
    val rawValue: String
)
