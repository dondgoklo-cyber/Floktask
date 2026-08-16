package com.taskmanager.presentation.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.TimelineEntry
import com.taskmanager.domain.usecase.timeline.GetUnifiedTimelineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class UnifiedTimelineUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<TimelineEntry> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UnifiedTimelineViewModel @Inject constructor(
    private val getUnifiedTimelineUseCase: GetUnifiedTimelineUseCase
) : ViewModel() {

    private val zone = ZoneId.systemDefault()
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val dayEntries = _selectedDate
        .flatMapLatest { date ->
            val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            getUnifiedTimelineUseCase(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val state: StateFlow<UnifiedTimelineUiState> = combine(
        _selectedDate,
        dayEntries
    ) { date, entries ->
        UnifiedTimelineUiState(selectedDate = date, entries = entries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UnifiedTimelineUiState())

    fun selectDate(date: LocalDate) { _selectedDate.value = date }
    fun previousDay() { _selectedDate.value = _selectedDate.value.minusDays(1) }
    fun nextDay() { _selectedDate.value = _selectedDate.value.plusDays(1) }
    fun goToToday() { _selectedDate.value = LocalDate.now() }
}
