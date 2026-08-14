package com.taskmanager.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.HabitRepository
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResults(
    val tasks: List<Task> = emptyList(),
    val projects: List<Project> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val hasQuery: Boolean = false
)

data class SearchUiState(
    val query: String = "",
    val results: SearchResults = SearchResults(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _taskResults = MutableStateFlow<List<Task>>(emptyList())
    private val _allProjects = MutableStateFlow<List<Project>>(emptyList())
    private val _allHabits = MutableStateFlow<List<Habit>>(emptyList())

    init {
        observeCatalogs()
    }

    private fun observeCatalogs() {
        viewModelScope.launch {
            projectRepository.getAllProjects().collect { _allProjects.value = it }
        }
        viewModelScope.launch {
            habitRepository.getAllHabits().collect { _allHabits.value = it }
        }
    }

    val state: StateFlow<SearchUiState> = combine(
        _query,
        _taskResults,
        combine(_allProjects, _allHabits) { p, h -> p to h }
    ) { query, tasks, (projects, habits) ->
        val q = query.trim()
        if (q.isEmpty()) {
            SearchUiState(query = query, results = SearchResults(hasQuery = false))
        } else {
            val filteredProjects = projects.filter {
                it.title.contains(q, ignoreCase = true) ||
                    (it.description?.contains(q, ignoreCase = true) == true)
            }
            val filteredHabits = habits.filter {
                it.name.contains(q, ignoreCase = true)
            }
            SearchUiState(
                query = query,
                results = SearchResults(
                    tasks = tasks,
                    projects = filteredProjects,
                    habits = filteredHabits,
                    hasQuery = true
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, SearchUiState())

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        val q = newQuery.trim()
        if (q.isEmpty()) {
            _taskResults.value = emptyList()
        } else {
            viewModelScope.launch {
                taskRepository.searchTasks(q).collect { results ->
                    _taskResults.value = results
                }
            }
        }
    }
}
