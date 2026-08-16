package com.taskmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing

/**
 * Палитра из 14 предопределённых цветов для тегов.
 * Хранится в БД как hex-строка (#RRGGBB).
 */
val TAG_COLORS: List<Color> = listOf(
    Color(0xFFEF5350), // красный
    Color(0xFFFF7043), // оранжево-красный
    Color(0xFFFFA726), // оранжевый
    Color(0xFFFFCA28), // янтарный
    Color(0xFF66BB6A), // зелёный
    Color(0xFF26A69A), // бирюзовый
    Color(0xFF29B6F6), // голубой
    Color(0xFF42A5F5), // синий
    Color(0xFF5C6BC0), // индиго
    Color(0xFF7E57C2), // фиолетовый
    Color(0xFFAB47BC), // пурпурный
    Color(0xFFEC407A), // розовый
    Color(0xFF8D6E63), // коричневый
    Color(0xFF78909C)  // сине-серый
)

/** Стандартный цвет тега (если в БД null) — оранжево-красный. */
val DEFAULT_TAG_COLOR: Color = Color(0xFFFF7043)

/**
 * Преобразует hex-строку (#RRGGBB или #AARRGGBB) в [Color].
 * Возвращает [DEFAULT_TAG_COLOR] при null или невалидном значении.
 */
fun parseTagColor(hex: String?): Color {
    if (hex.isNullOrBlank()) return DEFAULT_TAG_COLOR
    return runCatching {
        val clean = hex.removePrefix("#")
        val value = clean.toLong(16)
        // Поддержка #RRGGBB (6) и #AARRGGBB (8)
        if (clean.length <= 6) Color(value or 0xFF000000L)
        else Color(value)
    }.getOrElse { DEFAULT_TAG_COLOR }
}

/**
 * Палитра выбора цвета для тега.
 * Отображает 14 круглых кнопок, выбранная обводится.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TagColorPalette(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        TAG_COLORS.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (isSelected) Modifier.border(2.dp, AppTheme.colors.onSurface, CircleShape)
                        else Modifier
                    )
                    .clickable { onColorSelected(color) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
