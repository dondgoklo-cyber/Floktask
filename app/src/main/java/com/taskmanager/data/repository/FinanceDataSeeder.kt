package com.taskmanager.data.repository

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.repository.AccountRepository
import com.taskmanager.domain.repository.CategoryRepository
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заполняет БД категориями по умолчанию и основным счётом при первом запуске.
 * Идемпотентен: проверяет count() перед вставкой.
 */
@Singleton
class FinanceDataSeeder @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun seedIfNeeded() {
        scope.launch {
            if (categoryRepository.getCategoryCount() == 0) {
                seedDefaultCategories()
            }
            if (accountRepository.getAccountCount() == 0) {
                accountRepository.createAccount(
                    Account(name = "Основной", initialBalance = BigDecimal.ZERO, currency = "RUB")
                )
            }
        }
    }

    private suspend fun seedDefaultCategories() {
        val expenseCategories = listOf(
            "Продукты" to "#EF5350",
            "Транспорт" to "#29B6F6",
            "Жильё" to "#8D6E63",
            "Коммунальные услуги" to "#FFA726",
            "Здоровье" to "#66BB6A",
            "Развлечения" to "#AB47BC",
            "Покупки" to "#EC407A",
            "Подписки" to "#5C6BC0",
            "Образование" to "#26A69A",
            "Другое" to "#78909C"
        )
        val incomeCategories = listOf(
            "Зарплата" to "#66BB6A",
            "Фриланс" to "#42A5F5",
            "Инвестиции" to "#26A69A",
            "Подарки" to "#AB47BC",
            "Возврат" to "#FFA726",
            "Другое" to "#78909C"
        )

        expenseCategories.forEach { (name, color) ->
            categoryRepository.createCategory(
                Category(name = name, type = CategoryType.EXPENSE, color = color, isDefault = true)
            )
        }
        incomeCategories.forEach { (name, color) ->
            categoryRepository.createCategory(
                Category(name = name, type = CategoryType.INCOME, color = color, isDefault = true)
            )
        }
    }
}
