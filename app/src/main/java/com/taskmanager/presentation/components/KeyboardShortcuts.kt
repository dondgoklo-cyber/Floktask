package com.taskmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.type

/**
 * The keyboard actions a screen can handle (issue 18: power-user shortcuts).
 */
data class ShortcutActions(
    val onAddNew: () -> Unit = {},
    val onToggleSelected: () -> Unit = {},
    val onDeleteSelected: () -> Unit = {},
    val onMoveUp: () -> Unit = {},
    val onMoveDown: () -> Unit = {},
    val onSearch: () -> Unit = {}
)

/**
 * Installs keyboard-shortcut handling on a composable. Returns a Modifier to
 * apply to the screen root. Only triggers on KeyUp to avoid repeats.
 *
 * Shortcuts:
 *  - Ctrl+N / N: onAddNew
 *  - Space:       onToggleSelected
 *  - Delete:     onDeleteSelected
 *  - Arrow Up:    onMoveUp
 *  - Arrow Down:  onMoveDown
 *  - Ctrl+F / F:  onSearch
 */
@Composable
fun Modifier.handleKeyboardShortcuts(actions: ShortcutActions): Modifier {
    val captured = remember(actions) { actions }
    return this.onKeyEvent { event ->
        if (event.type != KeyEventType.KeyUp) return@onKeyEvent false
        val ctrl = event.isCtrlPressed
        val shift = event.isShiftPressed
        when {
            event.key == Key.N && (ctrl || !shift) -> { captured.onAddNew(); true }
            event.key == Key.F && (ctrl || !shift) -> { captured.onSearch(); true }
            event.key == Key.Spacebar && !ctrl && !shift -> { captured.onToggleSelected(); true }
            event.key == Key.Delete && !ctrl && !shift -> { captured.onDeleteSelected(); true }
            event.key == Key.DirectionUp && !ctrl && !shift -> { captured.onMoveUp(); true }
            event.key == Key.DirectionDown && !ctrl && !shift -> { captured.onMoveDown(); true }
            else -> false
        }
    }
}
