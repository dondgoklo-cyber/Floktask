package com.taskmanager.presentation.responsive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.navigation.Screen

/**
 * Adaptive navigation shell (issue 33): bottom bar on compact (phone),
 * navigation rail on tablet/expanded to use horizontal space.
 */
@Composable
fun AdaptiveNavScaffold(
    selectedRoute: String,
    onNavigate: (Screen) -> Unit,
    content: @Composable () -> Unit
) {
    val widthClass = currentWindowWidthClass()

    if (widthClass == WindowWidthClass.COMPACT) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            selected = screen.route == selectedRoute,
                            onClick = { onNavigate(screen) },
                            icon = { Icon(screen.navIcon, contentDescription = null) },
                            label = { Text(stringResource(screen.labelRes)) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) { content() }
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier.width(80.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Screen.bottomNavItems.forEach { screen ->
                    NavigationRailItem(
                        selected = screen.route == selectedRoute,
                        onClick = { onNavigate(screen) },
                        icon = { Icon(screen.navIcon, contentDescription = null) },
                        label = { Text(stringResource(screen.labelRes)) }
                    )
                }
            }
            Surface(modifier = Modifier.fillMaxSize()) { content() }
        }
    }
}

private val Screen.navIcon: ImageVector
    get() = when (this) {
        Screen.Today -> Icons.Filled.Checklist
        Screen.Projects -> Icons.Filled.Folder
        Screen.Finance -> Icons.Filled.AccountBalanceWallet
        Screen.Notes -> Icons.Filled.Description
        Screen.Habits -> Icons.Filled.Repeat
        else -> Icons.Filled.List
    }
