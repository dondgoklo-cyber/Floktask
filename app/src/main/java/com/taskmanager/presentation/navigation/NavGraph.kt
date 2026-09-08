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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.taskmanager.presentation.screens.notes.NotesScreen
import com.taskmanager.presentation.screens.profile.ProfileScreen
import com.taskmanager.presentation.screens.projectdetail.ProjectDetailScreen
import com.taskmanager.presentation.screens.projects.ProjectsScreen
import com.taskmanager.presentation.screens.search.SearchScreen
import com.taskmanager.presentation.screens.settings.SettingsScreen
import com.taskmanager.presentation.screens.tags.TagsScreen
import com.taskmanager.presentation.screens.today.TodayScreen
import com.taskmanager.presentation.screens.upcoming.UpcomingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            NavigationDrawerContent(
                sections = Screen.drawerSections,
                currentRoute = currentDestination?.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch { drawerState.close() }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
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
                composable(Screen.Inbox.route) { InboxScreen(onEditTask = { }) }
                composable(Screen.Calendar.route) { CalendarScreen() }
                composable(Screen.Habits.route) { HabitsScreen() }
                composable(Screen.Finance.route) { FinanceScreen() }
                composable(Screen.Focus.route) { FocusScreen() }
                composable(Screen.Insights.route) { InsightsScreen() }
                composable(Screen.Projects.route) {
                    ProjectsScreen(onProjectClick = { projectId -> navController.navigate("projectDetail/" + projectId) })
                }
                composable(Screen.Notes.route) { NotesScreen(onNoteClick = { }, onFolderClick = { }) }
                composable(Screen.Kanban.route) { KanbanScreen(onTaskClick = { }) }
                composable(Screen.Eisenhower.route) { EisenhowerScreen() }
                composable(Screen.Search.route) { SearchScreen(onBack = { navController.popBackStack() }, onTaskClick = { }) }
                composable(Screen.Upcoming.route) { UpcomingScreen(onEditTask = { }) }
                composable(Screen.Tags.route) { TagsScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.Profile.route) { ProfileScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
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
}

private val Screen.icon: ImageVector
    get() = when (this) {
        Screen.Today -> Icons.Filled.CheckCircle
        Screen.Inbox -> Icons.Filled.Inbox
        Screen.Calendar -> Icons.Filled.CalendarMonth
        Screen.Habits -> Icons.Filled.Repeat
        Screen.Finance -> Icons.Filled.AccountBalanceWallet
        Screen.Focus -> Icons.Filled.Timer
        Screen.Insights -> Icons.Filled.BarChart
        Screen.Projects -> Icons.Filled.Folder
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationDrawerContent(
    sections: List<DrawerSection>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    androidx.compose.material3.ModalDrawerSheet(
        modifier = androidx.compose.ui.Modifier.width(280.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            Text(
                text = "Меню",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            
            // Sections
            sections.forEach { section ->
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                section.items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    androidx.compose.material3.NavigationDrawerItem(
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = selected,
                        onClick = { onNavigate(screen.route) },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        modifier = androidx.compose.ui.Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
