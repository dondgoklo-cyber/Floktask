package com.taskmanager.presentation.navigation

import androidx.annotation.StringRes
import com.taskmanager.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Today : Screen("today", R.string.today)
    data object Inbox : Screen("inbox", R.string.inbox)
    data object Calendar : Screen("calendar", R.string.calendar)
    data object Habits : Screen("habits", R.string.habits)
    data object More : Screen("more", R.string.more)

    data object Plan : Screen("plan", R.string.plan)
    data object Focus : Screen("focus", R.string.focus)
    data object Insights : Screen("insights", R.string.insights)
    data object Projects : Screen("projects", R.string.projects)
    data object Finance : Screen("finance", R.string.finance)
    data object Notes : Screen("notes", R.string.notes)

    data object Upcoming : Screen("upcoming", R.string.upcoming)
    data object Search : Screen("search", R.string.search)
    data object Kanban : Screen("kanban", R.string.kanban)
    data object Tags : Screen("tags", R.string.tags)
    data object Eisenhower : Screen("eisenhower", R.string.eisenhower_matrix)
    data object Profile : Screen("profile", R.string.profile)
    data object Settings : Screen("settings", R.string.settings)
    data object ProjectDetail : Screen("projectDetail/{projectId}", R.string.projects)

    // Роуты с аргументами для редактирования
    data object TaskEdit : Screen("taskEdit/{taskId}", R.string.edit_task)
    data object NoteEdit : Screen("noteEdit/{noteId}", R.string.edit_note)
    data object FocusWithTask : Screen("focus?taskId={taskId}", R.string.focus)

    companion object {
        val bottomNavItems = listOf(Today, Inbox, Calendar, Habits, More)
    }
}