package com.taskmanager.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.ProductivityStats
import com.taskmanager.domain.usecase.analytics.GetProductivityStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    getProductivityStatsUseCase: GetProductivityStatsUseCase
) : ViewModel() {

    val stats: StateFlow<ProductivityStats?> = getProductivityStatsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
