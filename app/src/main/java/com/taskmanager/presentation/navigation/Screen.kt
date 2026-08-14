package com.taskmanager.presentation.navigation

import androidx.annotation.StringRes
import com.taskmanager.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Today : Screen("today", R.string.nav_today)
    data object Projects : Screen("projects", R.string.projects)
    data object Calendar : Screen("calendar", R.string.calendar)
    data object Habits : Screen("habits", R.string.habits)
    data object Focus : Screen("focus", R.string.focus)

    // Раздел "More" — открывается как отдельный экран
    data object More : Screen("more", R.string.more)

    // Дополнительные экраны (не в bottom nav)
    data object Inbox : Screen("inbox", R.string.inbox)
    data object Upcoming : Screen("upcoming", R.string.upcoming)
    data object Eisenhower : Screen("eisenhower", R.string.eisenhower_matrix)
    data object Statistics : Screen("statistics", R.string.statistics)
    data object Settings : Screen("settings", R.string.settings)

    data object TaskEditNew : Screen("task/new", R.string.add_task)

    data object TaskEdit : Screen("task/{taskId}", R.string.add_task) {
        const val ARG_TASK_ID = "taskId"
        fun buildRoute(taskId: Long): String = "task/$taskId"
    }

    companion object {
        /** Основная нижняя навигация: 5 ключевых разделов + More. */
        val bottomNavItems = listOf(Today, Projects, Calendar, Habits, Focus)
    }
}
