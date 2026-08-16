package com.taskmanager.domain.model

enum class CategoryType {
    INCOME, EXPENSE
}

data class Category(
    val id: Long? = null,
    val name: String,
    val type: CategoryType,
    val color: String? = null,
    val icon: String? = null,
    val isDefault: Boolean = false
)
