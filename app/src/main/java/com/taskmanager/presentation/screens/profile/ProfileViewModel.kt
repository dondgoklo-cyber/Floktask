package com.taskmanager.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Achievement
import com.taskmanager.domain.model.Achievements
import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.model.pointsForLevel
import com.taskmanager.domain.usecase.gamification.ObserveUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeUserStatsUseCase: ObserveUserStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeUserStatsUseCase()
            .onEach { stats ->
                _state.value = ProfileState.Loaded(stats ?: UserStats())
            }
            .catch { cause ->
                _state.value = ProfileState.Error(cause.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    fun achievementsFor(unlockedIds: List<String>): List<AchievementDisplay> =
        Achievements.all.map { ach ->
            AchievementDisplay(ach, ach.id in unlockedIds)
        }
}

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Loaded(val stats: UserStats) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

data class AchievementDisplay(
    val achievement: Achievement,
    val unlocked: Boolean
)

fun progressToNextLevel(stats: UserStats): Pair<Long, Long> {
    val currentLevelBase = pointsForLevel(stats.level)
    val nextLevelBase = pointsForLevel(stats.level + 1)
    val span = nextLevelBase - currentLevelBase
    val into = stats.totalPoints - currentLevelBase
    return into to span
}
