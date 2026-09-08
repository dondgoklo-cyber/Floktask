package com.taskmanager.presentation.components

/**
 * Унифицированное состояние UI для всех экранов.
 * Различает состояния: загрузка, пусто, ошибка, контент.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Content<T>(val data: T) : UiState<T>
}

/**
 * Helper-функции для работы с UiState.
 */
fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Empty -> UiState.Empty
    is UiState.Error -> UiState.Error(message)
    is UiState.Content -> UiState.Content(transform(data))
}

suspend fun <T> UiState<T>.flatMap(transform: suspend (T) -> UiState<R>): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Empty -> UiState.Empty
    is UiState.Error -> UiState.Error(message)
    is UiState.Content -> transform(data)
}

fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) action()
    return this
}

fun <T> UiState<T>.onEmpty(action: () -> Unit): UiState<T> {
    if (this is UiState.Empty) action()
    return this
}

fun <T> UiState<T>.onError(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) action(message)
    return this
}

fun <T> UiState<T>.onContent(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Content) action(data)
    return this
}
