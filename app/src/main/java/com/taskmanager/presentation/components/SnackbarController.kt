package com.taskmanager.presentation.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Глобальный SnackbarHostState для показа уведомлений с Undo.
 * Доступен через LocalSnackbarHostState из любого экрана.
 */
class SnackbarController(
    private val snackbarHostState: SnackbarHostState
) {
    /**
     * Показать snackbar с сообщением и кнопкой "Отменить".
     * @param message Текст сообщения
     * @param actionLabel Текст кнопки действия (по умолчанию "Отменить")
     * @param onUndo Callback при нажатии на кнопку отмены
     * @param duration Длительность отображения
     */
    suspend fun showWithUndo(
        message: String,
        actionLabel: String = "Отменить",
        onUndo: () -> Unit,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
            onUndo()
        }
    }

    /**
     * Показать простой snackbar без действия.
     */
    suspend fun show(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        snackbarHostState.showSnackbar(
            message = message,
            duration = duration
        )
    }
}

/**
 * CompositionLocal для доступа к SnackbarController из любого места в UI.
 */
val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided. Make sure NavGraph wraps content with Scaffold.")
}
