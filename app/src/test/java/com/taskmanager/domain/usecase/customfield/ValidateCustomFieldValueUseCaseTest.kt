package com.taskmanager.domain.usecase.customfield

import com.taskmanager.domain.usecase.validation.ValidationResult

import com.taskmanager.domain.model.CustomField
import com.taskmanager.domain.model.CustomFieldType
import com.taskmanager.domain.model.CustomFieldValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateCustomFieldValueUseCaseTest {

    private val useCase = ValidateCustomFieldValueUseCase()

    private fun field(type: CustomFieldType, options: List<String> = emptyList()) =
        CustomField(id = 1, name = "f", type = type, options = options)

    private fun value(raw: String) = CustomFieldValue(fieldId = 1, rawValue = raw)

    @Test
    fun `text field always valid`() {
        assertTrue(useCase(field(CustomFieldType.TEXT), value("anything")) is ValidationResult.Valid)
    }

    @Test
    fun `number field valid for numeric`() {
        assertTrue(useCase(field(CustomFieldType.NUMBER), value("42.5")) is ValidationResult.Valid)
    }

    @Test
    fun `number field invalid for non-numeric`() {
        assertTrue(useCase(field(CustomFieldType.NUMBER), value("abc")) is ValidationResult.Invalid)
    }

    @Test
    fun `dropdown valid when in options`() {
        val f = field(CustomFieldType.DROPDOWN, options = listOf("Low", "High"))
        assertTrue(useCase(f, value("High")) is ValidationResult.Valid)
    }

    @Test
    fun `dropdown invalid when not in options`() {
        val f = field(CustomFieldType.DROPDOWN, options = listOf("Low", "High"))
        assertTrue(useCase(f, value("Medium")) is ValidationResult.Invalid)
    }

    @Test
    fun `date valid for ISO-8601`() {
        assertTrue(
            useCase(field(CustomFieldType.DATE), value("2026-01-15T10:00:00Z")) is ValidationResult.Valid
        )
    }

    @Test
    fun `date invalid for garbage`() {
        assertTrue(useCase(field(CustomFieldType.DATE), value("yesterday")) is ValidationResult.Invalid)
    }

    @Test
    fun `empty value is valid (optional)`() {
        assertTrue(useCase(field(CustomFieldType.NUMBER), value("   ")) is ValidationResult.Valid)
    }
}
