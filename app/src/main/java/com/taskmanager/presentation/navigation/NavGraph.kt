package com.taskmanager.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskmanager.presentation.screens.calendar.CalendarScreen
import com.taskmanager.presentation.screens.eisenhower.EisenhowerScreen
import com.taskmanager.presentation.screens.finance.FinanceScreen
import com.taskmanager.presentation.screens.focus.FocusScreen
import com.taskmanager.presentation.screens.habits.HabitsScreen
import com.taskmanager.presentation.screens.inbox.InboxScreen
import com.taskmanager.presentation.screens.insights.InsightsScreen
import com.taskmanager.presentation.screens.kanban.KanbanScreen
import com.taskmanager.presentation.screens.more.MoreScreen
import com.taskmanager.presentation.screens.notes.NoteEditScreen
import com.taskmanager.presentation.screens.notes.NotesScreen
import com.taskmanager.presentation.screens.profile.ProfileScreen
import com.taskmanager.presentation.screens.projectdetail.ProjectDetailScreen
import com.taskmanager.presentation.screens.projects.ProjectsScreen
import com.taskmanager.presentation.screens.search.SearchScreen
import com.taskmanager.presentation.screens.settings.SettingsScreen
import com.taskmanager.presentation.screens.tags.TagsScreen
import com.taskmanager.presentation.screens.tasks.TaskEditScreen
import com.taskmanager.presentation.screens.today.TodayScreen
import com.taskmanager.presentation.screens.upcoming.UpcomingScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Screen.bottomNavItems.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = {
                            Text(
                                text = stringResource(screen.labelRes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Today.route) { TodayScreen() }
            composable(Screen.Inbox.route) { InboxScreen(onEditTask = { id -> navController.navigate("taskEdit/$id") }) }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Habits.route) { HabitsScreen() }
            composable(Screen.More.route) { MoreScreen(onNavigate = { route -> navController.navigate(route) }) }
            composable(Screen.Focus.route) { FocusScreen(focusTaskId = null) }
            composable(
                route = Screen.FocusWithTask.route,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType, nullable = true })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId")
                FocusScreen(focusTaskId = taskId)
            }
            composable(Screen.Insights.route) { InsightsScreen() }
            composable(Screen.Projects.route) {
                ProjectsScreen(onProjectClick = { projectId -> navController.navigate("projectDetail/" + projectId) })
            }
            composable(Screen.Finance.route) { FinanceScreen() }
            composable(Screen.Notes.route) { 
                NotesScreen(
                    onNoteClick = { id -> navController.navigate("noteEdit/$id") },
                    onFolderClick = { folderId -> /* handled internally in NotesScreen */ }
                )
            }
            composable(Screen.Kanban.route) { KanbanScreen(onTaskClick = { }) }
            composable(Screen.Eisenhower.route) { EisenhowerScreen() }
            composable(Screen.Search.route) { 
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onTaskClick = { },
                    onNoteClick = { id -> navController.navigate("noteEdit/$id") }
                )
            }
            composable(Screen.Upcoming.route) { UpcomingScreen(onEditTask = { id -> navController.navigate("taskEdit/$id") }) }
            composable(Screen.Tags.route) { TagsScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.Profile.route) { ProfileScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
            composable(
                route = Screen.TaskEdit.route,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                TaskEditScreen(taskId = taskId, onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.NoteEdit.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                NoteEditScreen(noteId = noteId, onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
                ProjectDetailScreen(
                    projectId = projectId,
                    onBack = { navController.popBackStack() },
                    onAddTask = { },
                    onTaskClick = { }
                )
            }
        }
    }
}

private val Screen.icon: ImageVector
    get() = when (this) {
        Screen.Today -> Icons.Filled.CheckCircle
        Screen.Inbox -> Icons.Filled.Inbox
        Screen.Calendar -> Icons.Filled.CalendarMonth
        Screen.Habits -> Icons.Filled.Repeat
        Screen.More -> Icons.Filled.MoreHoriz
        Screen.Plan -> Icons.Filled.CalendarMonth
        Screen.Focus -> Icons.Filled.Timer
        Screen.Insights -> Icons.Filled.BarChart
        Screen.Projects -> Icons.Filled.Folder
        Screen.Finance -> Icons.Filled.AccountBalanceWallet
        Screen.Notes -> Icons.Filled.Description
        Screen.Upcoming -> Icons.Filled.CalendarMonth
        Screen.Search -> Icons.Filled.Search
        Screen.Kanban -> Icons.Filled.ViewKanban
        Screen.Tags -> Icons.Filled.Label
        Screen.Eisenhower -> Icons.Filled.ViewKanban
        Screen.Profile -> Icons.Filled.Person
        Screen.Settings -> Icons.Filled.Settings
        Screen.ProjectDetail -> Icons.Filled.Folder
    }
