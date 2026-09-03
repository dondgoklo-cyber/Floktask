package com.taskmanager.presentation.navigation

import androidx.annotation.StringRes
import com.taskmanager.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Today : Screen("today", R.string.today)
    data object Plan : Screen("plan", R.string.plan)
    data object Focus : Screen("focus", R.string.focus)
    data object Insights : Screen("insights", R.string.insights)
    data object Projects : Screen("projects", R.string.projects)
    data object Finance : Screen("finance", R.string.finance)
    data object Notes : Screen("notes", R.string.notes)
    data object Habits : Screen("habits", R.string.habits)

    companion object {
        val bottomNavItems = listOf(Today, Plan, Focus, Insights)
    }
}
