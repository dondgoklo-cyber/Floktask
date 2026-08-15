package com.taskmanager.presentation.screens.tags

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.usecase.tag.CreateTagUseCase
import com.taskmanager.domain.usecase.tag.DeleteTagUseCase
import com.taskmanager.domain.usecase.tag.GetAllTagsUseCase
import com.taskmanager.domain.usecase.tag.UpdateTagUseCase
import com.taskmanager.presentation.components.DEFAULT_TAG_COLOR
import com.taskmanager.presentation.components.TAG_COLORS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TagsState {
    data object Loading : TagsState()
    data class Success(val tags: List<Tag>) : TagsState()
    data class Error(val message: String) : TagsState()
}

/** Состояние диалога создания/редактирования тега. */
data class TagDialogState(
    val show: Boolean = false,
    val editingTag: Tag? = null,
    val name: String = "",
    val colorHex: String? = null,
    val nameError: Boolean = false
) {
    val isOpen: Boolean get() = show
}

@HiltViewModel
class TagsViewModel @Inject constructor(
    getAllTagsUseCase: GetAllTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : ViewModel() {

    private val _tagsState = MutableStateFlow<TagsState>(TagsState.Loading)
    val tagsState: StateFlow<TagsState> = _tagsState.asStateFlow()

    private val _dialogState = MutableStateFlow(TagDialogState())
    val dialogState: StateFlow<TagDialogState> = _dialogState.asStateFlow()

    init {
        getAllTagsUseCase()
            .onEach { tags -> _tagsState.value = TagsState.Success(tags) }
            .catch { cause -> _tagsState.value = TagsState.Error(cause.message ?: "Ошибка загрузки") }
            .launchIn(viewModelScope)
    }

    fun openCreateDialog() {
        _dialogState.value = TagDialogState(show = true)
    }

    fun openEditDialog(tag: Tag) {
        _dialogState.value = TagDialogState(
            show = true,
            editingTag = tag,
            name = tag.name,
            colorHex = tag.color
        )
    }

    fun closeDialog() {
        _dialogState.value = TagDialogState()
    }

    fun onNameChange(value: String) {
        _dialogState.value = _dialogState.value.copy(name = value, nameError = false)
    }

    fun onColorChange(color: Color) {
        _dialogState.value = _dialogState.value.copy(colorHex = colorToHex(color))
    }

    fun saveTag() {
        val state = _dialogState.value
        val name = state.name.trim()
        if (name.isBlank()) {
            _dialogState.value = state.copy(nameError = true)
            return
        }
        viewModelScope.launch {
            val colorHex = state.colorHex ?: colorToHex(DEFAULT_TAG_COLOR)
            state.editingTag?.let { existing ->
                updateTagUseCase(existing.copy(name = name, color = colorHex))
            } ?: createTagUseCase(Tag(name = name, color = colorHex))
            closeDialog()
        }
    }

    fun deleteTag(id: Long) {
        viewModelScope.launch {
            deleteTagUseCase(id)
        }
    }
}

/** Преобразует Color в hex-строку без альфа-канала (#RRGGBB) для хранения в БД. */
fun colorToHex(color: Color): String {
    val r = (color.red * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue * 255).toInt()
    return String.format("#%02X%02X%02X", r, g, b)
}

/** Проверяет, входит ли цвет в стандартную палитру тегов. */
fun isPaletteColor(color: Color): Boolean = TAG_COLORS.any { it == color }
