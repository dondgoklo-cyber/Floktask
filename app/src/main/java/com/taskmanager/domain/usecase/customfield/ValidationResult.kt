package com.taskmanager.domain.usecase.customfield

/**
 * Outcome of a validation. (Local mirror of the validation-layer type;
 * consolidated on merge with PR #24.)
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}
