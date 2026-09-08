package com.taskmanager.presentation.navigation

import androidx.annotation.StringRes
import com.taskmanager.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Today : Screen("today", R.string.today)
    data object Inbox : Screen("inbox", R.string.inbox)
    data object Calendar : Screen("calendar", R.string.calendar)
    data object Habits : Screen("habits", R.string.habits)
    data object Finance : Screen("finance", R.string.finance)

    data object Focus : Screen("focus", R.string.focus)
    data object Insights : Screen("insights", R.string.insights)
    data object Projects : Screen("projects", R.string.projects)
    data object Notes : Screen("notes", R.string.notes)
    data object Kanban : Screen("kanban", R.string.kanban)
    data object Tags : Screen("tags", R.string.tags)
    data object Eisenhower : Screen("eisenhower", R.string.eisenhower_matrix)
    data object Profile : Screen("profile", R.string.profile)
    data object Settings : Screen("settings", R.string.settings)
    data object ProjectDetail : Screen("projectDetail/{projectId}", R.string.projects)

    companion object {
        /** 5 фиксированных + 1 настраиваемый слот (по умолчанию Finance) */
        val bottomNavItems = listOf(Today, Inbox, Calendar, Habits, Finance)
        
        /** Пункты для navigation drawer, сгруппированные по секциям */
        val drawerSections = listOf(
            DrawerSection(
                title = "Обзор",
                items = listOf(Search, Insights, Focus, Upcoming)
            ),
            DrawerSection(
                title = "Рабочее пространство",
                items = listOf(Projects, Kanban, Notes, Tags, Eisenhower)
            ),
            DrawerSection(
                title = "Аккаунт",
                items = listOf(Profile, Settings)
            )
        )
    }
}

data class DrawerSection(
    val title: String,
    val items: List<Screen>
)