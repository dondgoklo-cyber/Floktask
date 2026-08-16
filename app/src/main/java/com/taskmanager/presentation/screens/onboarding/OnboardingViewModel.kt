package com.taskmanager.presentation.screens.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isCompleted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        OnboardingUiState(isCompleted = prefs.getBoolean(KEY_COMPLETED, false))
    )
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun nextPage(totalPages: Int) {
        val next = (_state.value.currentPage + 1).coerceAtMost(totalPages - 1)
        _state.value = _state.value.copy(currentPage = next)
    }

    fun setPage(index: Int) {
        _state.value = _state.value.copy(currentPage = index)
    }

    fun complete() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
        _state.value = _state.value.copy(isCompleted = true)
    }

    companion object {
        const val PREFS_NAME = "onboarding_prefs"
        const val KEY_COMPLETED = "onboarding_completed"
    }
}
