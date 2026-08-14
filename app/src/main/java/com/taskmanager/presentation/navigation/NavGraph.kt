package com.taskmanager.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskmanager.presentation.screens.calendar.CalendarScreen
import com.taskmanager.presentation.screens.profile.ProfileScreen
import com.taskmanager.presentation.screens.projects.ProjectsScreen
import com.taskmanager.presentation.screens.tasks.TaskEditScreen
import com.taskmanager.presentation.screens.tasks.TasksScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val navRoutes = Screen.bottomNavItems.map { it.route }.toSet()
    val showBottomBar = navRoutes.contains(currentRoute)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
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
                            label = { Text(stringResource(screen.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tasks.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(120)) },
            exitTransition = { fadeOut(animationSpec = tween(80)) },
            popEnterTransition = { fadeIn(animationSpec = tween(120)) },
            popExitTransition = { fadeOut(animationSpec = tween(80)) }
        ) {
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onTaskClick = { taskId -> navController.navigate(Screen.TaskEdit.buildRoute(taskId)) },
                    onAddTaskClick = { navController.navigate(Screen.TaskEditNew.route) }
                )
            }
            composable(Screen.Projects.route) { ProjectsScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.TaskEditNew.route) {
                TaskEditScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.TaskEdit.route,
                arguments = listOf(navArgument(Screen.TaskEdit.ARG_TASK_ID) { type = NavType.LongType })
            ) {
                TaskEditScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private val Screen.icon: ImageVector
    get() = when (this) {
        Screen.Tasks -> Icons.Filled.List
        Screen.Projects -> Icons.Filled.Folder
        Screen.Calendar -> Icons.Filled.DateRange
        Screen.Profile -> Icons.Filled.Person
        Screen.TaskEditNew,
        Screen.TaskEdit -> Icons.Filled.List
    }
