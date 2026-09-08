package com.taskmanager.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskmanager.R
import com.taskmanager.domain.model.Priority
import com.taskmanager.presentation.components.CreateMenuSheet
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
import com.taskmanager.presentation.screens.tasks.QuickAddSheet
import com.taskmanager.presentation.screens.tasks.TaskEditScreen
import com.taskmanager.presentation.screens.today.TodayScreen
import com.taskmanager.presentation.screens.upcoming.UpcomingScreen
import com.taskmanager.presentation.screens.voice.VoiceTaskSheet
import com.taskmanager.presentation.viewmodel.tasks.QuickAddViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val scope = rememberCoroutineScope()
    
    // Global states for sheets
    var showCreateMenu by remember { mutableStateOf(false) }
    var showQuickAdd by remember { mutableStateOf(false) }
    var showVoiceSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val quickAddViewModel: QuickAddViewModel = hiltViewModel()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Today
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Today.route } == true,
                        onClick = {
                            navController.navigate(Screen.Today.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Screen.Today.icon, contentDescription = null) },
                        label = { Text(stringResource(Screen.Today.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    // Inbox
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Inbox.route } == true,
                        onClick = {
                            navController.navigate(Screen.Inbox.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Screen.Inbox.icon, contentDescription = null) },
                        label = { Text(stringResource(Screen.Inbox.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    // Center + button with long click for voice
                    FilledIconButton(
                        onClick = { showCreateMenu = true },
                        modifier = Modifier
                            .size(56.dp)
                            .combinedClickable(
                                onClick = { showCreateMenu = true },
                                onLongClick = { showVoiceSheet = true }
                            ),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.create), modifier = Modifier.size(28.dp))
                    }
                    // Calendar
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Calendar.route } == true,
                        onClick = {
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Screen.Calendar.icon, contentDescription = null) },
                        label = { Text(stringResource(Screen.Calendar.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    // More
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.More.route } == true,
                        onClick = {
                            navController.navigate(Screen.More.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Screen.More.icon, contentDescription = null) },
                        label = { Text(stringResource(Screen.More.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
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
            composable(Screen.Today.route) { 
                TodayScreen(
                    onEditTask = { id -> navController.navigate("taskEdit/$id") },
                    onStartFocus = { id -> navController.navigate("focus?taskId=$id") }
                ) 
            }
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
            composable(Screen.Kanban.route) { 
                KanbanScreen(
                    onTaskClick = { id -> /* открывается TaskDetailSheet внутри экрана */ },
                    onEditTask = { id -> navController.navigate("taskEdit/$id") }
                ) 
            }
            composable(Screen.Eisenhower.route) { EisenhowerScreen() }
            composable(Screen.Search.route) { 
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onTaskClick = { id -> /* открывается TaskDetailSheet внутри экрана */ },
                    onEditTask = { id -> navController.navigate("taskEdit/$id") },
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
                    onAddTask = { showQuickAdd = true },
                    onTaskClick = { id -> /* открывается TaskDetailSheet внутри экрана */ },
                    onEditTask = { id -> navController.navigate("taskEdit/$id") }
                )
            }
        }
    }
    
    // CreateMenuSheet
    if (showCreateMenu) {
        CreateMenuSheet(
            onDismiss = { showCreateMenu = false },
            onTask = { showQuickAdd = true },
            onHabit = { navController.navigate(Screen.Habits.route) },
            onIncome = { navController.navigate(Screen.Finance.route) },
            onExpense = { navController.navigate(Screen.Finance.route) },
            onProject = { navController.navigate(Screen.Projects.route) },
            onNote = { navController.navigate("noteEdit/0") },
            onVoice = { showVoiceSheet = true }
        )
    }
    
    // QuickAddSheet
    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onCreated = { id ->
                showQuickAdd = false
                scope.launch {
                    snackbarHostState.showSnackbar("Задача создана")
                }
            }
        )
    }
    
    // VoiceTaskSheet
    if (showVoiceSheet) {
        VoiceTaskSheet(
            onDismiss = { showVoiceSheet = false },
            onCreate = { title, date, time, priority, recurrence ->
                quickAddViewModel.createTaskFromVoice(title, date, time, priority, recurrence) { id ->
                    showVoiceSheet = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Задача создана")
                    }
                }
            }
        )
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
