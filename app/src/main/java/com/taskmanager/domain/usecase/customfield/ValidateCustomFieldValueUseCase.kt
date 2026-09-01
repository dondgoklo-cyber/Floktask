package com.taskmanager.domain.usecase.customfield

import com.taskmanager.domain.model.CustomField
import com.taskmanager.domain.model.CustomFieldType
import com.taskmanager.domain.model.CustomFieldValue
import com.taskmanager.domain.usecase.validation.ValidationResult
import java.time.Instant
import javax.inject.Inject

/**
 * Validates a [CustomFieldValue] against its [CustomField] type (issue 39).
 */
class ValidateCustomFieldValueUseCase @Inject constructor() {

    operator fun invoke(field: CustomField, value: CustomFieldValue): ValidationResult {
        val raw = value.rawValue.trim()
        if (raw.isEmpty()) return ValidationResult.Valid // optional by default
        return when (field.type) {
            CustomFieldType.TEXT -> ValidationResult.Valid
            CustomFieldType.NUMBER -> validateNumber(raw)
            CustomFieldType.DROPDOWN -> validateDropdown(field, raw)
            CustomFieldType.DATE -> validateDate(raw)
        }
    }

    private fun validateNumber(raw: String): ValidationResult =
        if (raw.toDoubleOrNull() != null) ValidationResult.Valid
        else ValidationResult.Invalid("'$raw' is not a valid number")

    private fun validateDropdown(field: CustomField, raw: String): ValidationResult =
        if (field.options.isEmpty() || raw in field.options) ValidationResult.Valid
        else ValidationResult.Invalid("'$raw' is not a valid option")

    private fun validateDate(raw: String): ValidationResult =
        runCatching { Instant.parse(raw) }
            .map { ValidationResult.Valid }
            .getOrElse { ValidationResult.Invalid("'$raw' is not a valid ISO-8601 date") }
}
