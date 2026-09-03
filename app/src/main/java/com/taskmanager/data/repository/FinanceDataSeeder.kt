package com.taskmanager.data.repository

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.repository.AccountRepository
import com.taskmanager.domain.repository.CategoryRepository
import com.taskmanager.utils.toMoneyBigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Заполняет БД категориями по умолчанию при первом запуске.
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
                    Account(name = "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439", initialBalance = 0.0.toMoneyBigDecimal(), currency = "RUB")
                )
            }
        }
    }

    private suspend fun seedDefaultCategories() {
        val expenseCategories = listOf(
            "\u041f\u0440\u043e\u0434\u0443\u043a\u0442\u044b" to "#EF5350",
            "\u0422\u0440\u0430\u043d\u0441\u043f\u043e\u0440\u0442" to "#29B6F6",
            "\u0416\u0438\u043b\u044c\u0451" to "#8D6E63",
            "\u041a\u043e\u043c\u043c\u0443\u043d\u0430\u043b\u044c\u043d\u044b\u0435 \u0443\u0441\u043b\u0443\u0433\u0438" to "#FFA726",
            "\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435" to "#66BB6A",
            "\u0420\u0430\u0437\u0432\u043b\u0435\u0447\u0435\u043d\u0438\u044f" to "#AB47BC",
            "\u041f\u043e\u043a\u0443\u043f\u043a\u0438" to "#EC407A",
            "\u041f\u043e\u0434\u043f\u0438\u0441\u043a\u0438" to "#5C6BC0",
            "\u041e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u043d\u0438\u0435" to "#26A69A",
            "\u0414\u0440\u0443\u0433\u043e\u0435" to "#78909C"
        )
        val incomeCategories = listOf(
            "\u0417\u0430\u0440\u043f\u043b\u0430\u0442\u0430" to "#66BB6A",
            "\u0424\u0440\u0438\u043b\u0430\u043d\u0441" to "#42A5F5",
            "\u0418\u043d\u0432\u0435\u0441\u0442\u0438\u0446\u0438\u0438" to "#26A69A",
            "\u041f\u043e\u0434\u0430\u0440\u043a\u0438" to "#AB47BC",
            "\u0412\u043e\u0437\u0432\u0440\u0430\u0442" to "#FFA726",
            "\u0414\u0440\u0443\u0433\u043e\u0435" to "#78909C"
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
