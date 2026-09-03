# ADR-006: Архитектура Timeline View для Floktask

## 📅 Методология
- **Статус:** Accepted
- **Дата:** 2026-09-15
- **Автор:** Architect
- **Участники:** Product Manager, Backend, Frontend, UI/UX Designer

## 🎯 Контекст и проблема

### Контекст
Timeline View — это ключевая фича для визуализации задач во временной шкале. Пользователи хотят видеть:
- Все задачи на временной линии
- Пересечения и конфликты задач
- Свободные и занятые периоды
- Возможность drag-and-drop для изменения дат
- Группировку по проектам, тегам, пользователям

Это критично для:
- Планирования рабочего времени
- Визуализации нагрузки
- Обнаружения конфликтов расписания
- Координации между командами

### Проблема
- ❌ Нет backend поддержки для Timeline View
- ❌ Нет эффективных запросов для временных диапазонов
- ❌ Нет механизма обнаружения конфликтов
- ❌ Нет поддержки drag-and-drop операций

### Драйверы (что нас мотивирует)
- **Пользовательский опыт** — Интуитивная визуализация временных данных
- **Производительность** — Быстрая загрузка и рендеринг
- **Коллаборация** — Обнаружение конфликтов между пользователями
- **Гибкость** — Поддержка разных режимов отображения
- **Конкурентоспособность** — Аналогичные фичи есть в Todoist, TickTick, Notion

## ⚖️ Варианты решения

### Вариант 1: Client-Side Rendering
Все вычисления на клиенте, сервер только отдает сырые данные.

**Плюсы:**
- ✅ Простота backend реализации
- ✅ Гибкость отображения
- ✅ Нет нагрузки на сервер

**Минусы:**
- ❌ Сложность обработки больших объемов данных
- ❌ Проблемы с производительностью на мобильных устройствах
- ❌ Сложность обнаружения конфликтов
- ❌ Нет серверной валидации

**Оценка:**
- **Сложность реализации:** Medium
- **Стоимость:** $
- **Время:** 2 недели
- **Риск:** Medium

### Вариант 2: Server-Side Rendering
Сервер генерирует HTML/SVG для timeline.

**Плюсы:**
- ✅ Быстрый рендеринг на клиенте
- ✅ Серверная валидация
- ✅ Обнаружение конфликтов на сервере

**Минусы:**
- ❌ Высокая нагрузка на сервер
- ❌ Сложность масштабирования
- ❌ Ограниченная интерактивность
- ❌ Проблемы с кэшированием

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4 недели
- **Риск:** High

### Вариант 3: Hybrid Approach
Сервер предоставляет оптимизированные данные, клиент рендерит.

**Плюсы:**
- ✅ Оптимальная производительность
- ✅ Гибкость отображения
- ✅ Серверная валидация
- ✅ Масштабируемость

**Минусы:**
- ❌ Сложность реализации
- ❌ Необходимость координации клиент-сервер

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 3 недели
- **Риск:** Medium

## 🎯 Выбранное решение
**Hybrid Approach** — Сервер предоставляет оптимизированные данные, клиент рендерит.

### Архитектура

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Timeline View Architecture                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Client Side (Mobile/Web)                        │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐  │   │
│  │  │  Timeline   │    │  Conflict   │    │  Drag-and-Drop          │  │   │
│  │  │  Renderer  │    │  Detector   │    │  Handler               │  │   │
│  │  │  (Compose/ │    │  (Client)   │    │  (Client)              │  │   │
│  │  │   React)   │    └─────────────┘    └─────────────────────────┘  │   │
│  │  └─────────────┘                                                  │   │
│  │        │                                                          │   │
│  │        ▼                                                          │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Timeline State Manager                      │ │   │
│  │  │  - Time range management                                        │ │   │
│  │  │  - Zoom level control                                           │ │   │
│  │  │  - Grouping configuration                                        │ │   │
│  │  │  - Filtering                                                     │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      API Gateway (Spring Cloud Gateway)               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Timeline Service                              │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐  │   │
│  │  │  Timeline   │    │  Conflict   │    │  Optimization           │  │   │
│  │  │  Query     │    │  Detector   │    │  Engine                │  │   │
│  │  │  Engine    │    │  (Server)   │    │                         │  │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│         ┌──────────────────────────┬──────────────────────────┐         │
│         ▼                          ▼                          ▼                │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐      │
│  │  Task       │           │  Project    │           │  User       │      │
│  │  Service    │           │  Service    │           │  Service    │      │
│  └─────────────┘           └─────────────┘           └─────────────┘      │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        PostgreSQL Cluster                              │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐ │   │
│  │  │  Timeline   │    │  Task       │    │  Indexes               │ │   │
│  │  │  Cache     │    │  Database   │    │  (Optimized)           │ │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Компоненты

#### 1. Timeline Service

**Ответственность:**
- Обработка запросов для Timeline View
- Оптимизация данных для клиента
- Обнаружение конфликтов
- Кэширование часто запрашиваемых диапазонов

**Endpoints:**
```
GET    /api/v1/timeline/tasks              # Получение задач для timeline
GET    /api/v1/timeline/conflicts          # Обнаружение конфликтов
POST   /api/v1/timeline/tasks/move        # Перемещение задачи (drag-and-drop)
POST   /api/v1/timeline/tasks/resize      # Изменение длительности задачи
GET    /api/v1/timeline/groups             # Группировка задач
```

#### 2. Timeline Query Engine

**Функционал:**
- Запрос задач в заданном временном диапазоне
- Фильтрация по проектам, тегам, пользователям
- Группировка по разным критериям
- Оптимизация запросов к БД

**SQL Пример:**
```sql
SELECT 
    t.id, t.uuid, t.title, t.start_date, t.due_date, 
    t.duration_minutes, t.project_id, t.priority, t.status,
    p.title as project_title, p.color as project_color
FROM tasks t
LEFT JOIN projects p ON t.project_id = p.id
WHERE 
    t.user_id = ? 
    AND (
        (t.start_date BETWEEN ? AND ?) OR
        (t.due_date BETWEEN ? AND ?) OR
        (t.start_date <= ? AND t.due_date >= ?)
    )
    AND t.deleted_at IS NULL
ORDER BY t.start_date, t.due_date
LIMIT 500
```

#### 3. Conflict Detector

**Алгоритм обнаружения конфликтов:**

```kotlin
class ConflictDetector {
    
    data class TimeRange(
        val start: Instant,
        val end: Instant
    )
    
    data class Conflict(
        val task1: Task,
        val task2: Task,
        val overlap: TimeRange
    )
    
    fun detectConflicts(tasks: List<Task>): List<Conflict> {
        val conflicts = mutableListOf<Conflict>()
        
        for (i in tasks.indices) {
            for (j in i + 1 until tasks.size) {
                val task1 = tasks[i]
                val task2 = tasks[j]
                
                // Пропускаем задачи из одного проекта (если не нужно проверять)
                if (task1.projectId == task2.projectId && 
                    !task1.checkCrossProjectConflicts) {
                    continue
                }
                
                val range1 = TimeRange(task1.startDate, task1.dueDate)
                val range2 = TimeRange(task2.startDate, task2.dueDate)
                
                val overlap = findOverlap(range1, range2)
                
                if (overlap != null) {
                    conflicts.add(Conflict(task1, task2, overlap))
                }
            }
        }
        
        return conflicts
    }
    
    private fun findOverlap(range1: TimeRange, range2: TimeRange): TimeRange? {
        val start = max(range1.start, range2.start)
        val end = min(range1.end, range2.end)
        
        return if (start < end) TimeRange(start, end) else null
    }
}
```

#### 4. Optimization Engine

**Оптимизации:**
- **Pagination**: Постраничная загрузка задач
- **Lazy Loading**: Подгрузка при скролле
- **Caching**: Кэширование часто запрашиваемых диапазонов
- **Pre-fetching**: Предзагрузка соседних диапазонов

**Кэш стратегия:**
```
Key: timeline:{userId}:{startDate}:{endDate}:{groupBy}:{filters}
TTL: 5 minutes
Invalidation: On task create/update/delete
```

### Data Model

#### Timeline Task DTO

```kotlin
@Serializable
data class TimelineTaskDto(
    val id: Long,
    val uuid: String,
    val title: String,
    val description: String? = null,
    
    // Time range
    val startDate: Instant,
    val dueDate: Instant? = null,
    val durationMinutes: Int? = null,
    
    // Visual properties
    val projectId: Long? = null,
    val projectTitle: String? = null,
    val projectColor: String? = null,
    val priority: TaskPriority,
    val status: TaskStatus,
    
    // Position
    val left: Double,      // % position from start
    val width: Double,     // % width of timeline
    val top: Int,          // Row number
    
    // Conflict info
    val hasConflict: Boolean = false,
    val conflictsWith: List<Long> = emptyList(),
    
    // Metadata
    val tags: List<String> = emptyList(),
    val assigneeId: Long? = null,
    val assigneeName: String? = null,
    val assigneeAvatar: String? = null
)
```

#### Timeline Group DTO

```kotlin
@Serializable
data class TimelineGroupDto(
    val id: String,
    val title: String,
    val type: GroupType,  // PROJECT, TAG, USER, DAY, WEEK, MONTH
    val color: String? = null,
    val tasks: List<TimelineTaskDto> = emptyList(),
    val startDate: Instant? = null,
    val endDate: Instant? = null
)

enum class GroupType {
    PROJECT, TAG, USER, DAY, WEEK, MONTH, CUSTOM
}
```

#### Timeline Response

```kotlin
@Serializable
data class TimelineResponse(
    val timeRange: TimeRangeDto,
    val groups: List<TimelineGroupDto> = emptyList(),
    val conflicts: List<ConflictDto> = emptyList(),
    val statistics: TimelineStatisticsDto,
    val metadata: TimelineMetadataDto
)

@Serializable
data class TimelineStatisticsDto(
    val totalTasks: Int,
    val completedTasks: Int,
    val overdueTasks: Int,
    val conflictCount: Int,
    val totalDurationHours: Double,
    val utilizationPercentage: Double
)

@Serializable
data class TimelineMetadataDto(
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val hasMore: Boolean,
    val zoomLevel: ZoomLevel,
    val groupBy: GroupType
)

enum class ZoomLevel {
    HOUR, DAY, WEEK, MONTH, QUARTER, YEAR
}
```

### API Endpoints

#### GET /api/v1/timeline/tasks

**Параметры:**
```
userId: Long (optional, default: current user)
startDate: Instant (required)
endDate: Instant (required)
groupBy: GroupType (optional, default: DAY)
filters: TimelineFilterDto (optional)
zoomLevel: ZoomLevel (optional, default: DAY)
page: Int (optional, default: 0)
pageSize: Int (optional, default: 100)
```

**Ответ:**
```json
{
  "timeRange": {
    "start": "2026-09-01T00:00:00Z",
    "end": "2026-09-07T23:59:59Z"
  },
  "groups": [
    {
      "id": "2026-09-01",
      "title": "Понедельник, 1 сентября",
      "type": "DAY",
      "tasks": [...]
    }
  ],
  "conflicts": [],
  "statistics": {
    "totalTasks": 25,
    "completedTasks": 5,
    "overdueTasks": 2,
    "conflictCount": 1,
    "totalDurationHours": 45.5,
    "utilizationPercentage": 65.2
  },
  "metadata": {
    "page": 0,
    "pageSize": 100,
    "totalPages": 1,
    "hasMore": false,
    "zoomLevel": "DAY",
    "groupBy": "DAY"
  }
}
```

#### GET /api/v1/timeline/conflicts

**Параметры:**
```
taskId: Long (required)
startDate: Instant (optional)
endDate: Instant (optional)
```

**Ответ:**
```json
{
  "task": {...},
  "conflicts": [
    {
      "withTask": {...},
      "overlap": {
        "start": "2026-09-02T10:00:00Z",
        "end": "2026-09-02T12:00:00Z"
      },
      "type": "TIME_OVERLAP"
    }
  ]
}
```

#### POST /api/v1/timeline/tasks/move

**Запрос:**
```json
{
  "taskId": 123,
  "newStartDate": "2026-09-03T10:00:00Z",
  "newDueDate": "2026-09-03T12:00:00Z",
  "checkConflicts": true
}
```

**Ответ:**
```json
{
  "success": true,
  "task": {...},
  "conflicts": [],
  "warnings": []
}
```

#### POST /api/v1/timeline/tasks/resize

**Запрос:**
```json
{
  "taskId": 123,
  "newDurationMinutes": 180,
  "keepStartDate": true
}
```

### Клиентская часть

#### Timeline Renderer (Compose)

```kotlin
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onTaskClick: (Task) -> Unit,
    onTaskDrag: (Task, Offset) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header with navigation
        TimelineHeader(
            timeRange = state.timeRange,
            zoomLevel = state.zoomLevel,
            onZoomChange = viewModel::changeZoom,
            onNavigate = viewModel::navigate
        )
        
        // Timeline grid
        TimelineGrid(
            groups = state.groups,
            timeRange = state.timeRange,
            zoomLevel = state.zoomLevel,
            onTaskClick = onTaskClick,
            onTaskDrag = onTaskDrag
        )
        
        // Conflict indicator
        if (state.conflicts.isNotEmpty()) {
            ConflictIndicator(
                conflicts = state.conflicts,
                onResolve = viewModel::resolveConflict
            )
        }
    }
}

@Composable
fun TimelineGrid(
    groups: List<TimelineGroupDto>,
    timeRange: TimeRangeDto,
    zoomLevel: ZoomLevel,
    onTaskClick: (Task) -> Unit,
    onTaskDrag: (Task, Offset) -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Horizontal scroll for time
    val timeScrollState = rememberScrollState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Time header
        TimeHeader(
            timeRange = timeRange,
            zoomLevel = zoomLevel,
            scrollState = timeScrollState
        )
        
        // Groups
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(groups) { group ->
                TimelineGroup(
                    group = group,
                    timeRange = timeRange,
                    zoomLevel = zoomLevel,
                    timeScrollState = timeScrollState,
                    onTaskClick = onTaskClick,
                    onTaskDrag = onTaskDrag
                )
            }
        }
    }
}

@Composable
fun TimelineGroup(
    group: TimelineGroupDto,
    timeRange: TimeRangeDto,
    zoomLevel: ZoomLevel,
    timeScrollState: ScrollState,
    onTaskClick: (Task) -> Unit,
    onTaskDrag: (Task, Offset) -> Unit
) {
    val groupHeight = 60.dp
    
    Row(
        modifier = Modifier
            .height(groupHeight)
            .fillMaxWidth()
    ) {
        // Group label
        Text(
            text = group.title,
            modifier = Modifier
                .width(150.dp)
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyMedium
        )
        
        // Timeline area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(groupHeight)
                .horizontalScroll(timeScrollState)
        ) {
            // Time markers
            TimeMarkers(timeRange, zoomLevel)
            
            // Tasks
            group.tasks.forEach { task ->
                TimelineTask(
                    task = task,
                    timeRange = timeRange,
                    zoomLevel = zoomLevel,
                    onClick = { onTaskClick(task) },
                    onDrag = { offset -> onTaskDrag(task, offset) }
                )
            }
            
            // Conflict indicators
            group.tasks
                .filter { it.hasConflict }
                .forEach { task ->
                    ConflictIndicator(task = task)
                }
        }
    }
}

@Composable
fun TimelineTask(
    task: TimelineTaskDto,
    timeRange: TimeRangeDto,
    zoomLevel: ZoomLevel,
    onClick: () -> Unit,
    onDrag: (Offset) -> Unit
) {
    val taskWidth = remember(task, timeRange) {
        calculateTaskWidth(task, timeRange)
    }
    
    val taskOffset = remember(task, timeRange) {
        calculateTaskOffset(task, timeRange)
    }
    
    Box(
        modifier = Modifier
            .offset { IntOffset(taskOffset.x.roundToInt(), 0) }
            .width(taskWidth)
            .height(40.dp)
            .background(
                color = when (task.priority) {
                    TaskPriority.HIGH -> Color.Red
                    TaskPriority.MEDIUM -> Color.Orange
                    TaskPriority.LOW -> Color.Green
                    else -> Color.Gray
                },
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = if (task.hasConflict) 2.dp else 0.dp,
                color = if (task.hasConflict) Color.Red else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .draggable(
                orientation = Orientation.Horizontal,
                onDrag = { change, offset ->
                    change.consume()
                    onDrag(offset)
                }
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = task.title,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
```

### Drag-and-Drop Реализация

```kotlin
class TimelineViewModel(
    private val timelineService: TimelineService,
    private val taskService: TaskService
) : ViewModel() {
    
    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()
    
    private var dragStartPosition: Offset? = null
    private var dragStartTime: Instant? = null
    private var draggedTask: TimelineTaskDto? = null
    
    fun onTaskDragStart(task: TimelineTaskDto, position: Offset) {
        dragStartPosition = position
        dragStartTime = task.startDate
        draggedTask = task
    }
    
    fun onTaskDrag(task: TimelineTaskDto, offset: Offset) {
        if (dragStartPosition == null || dragStartTime == null) return
        
        val dragStart = dragStartPosition!!
        val timeRange = state.value.timeRange
        
        // Calculate time delta based on pixel offset
        val pixelsPerHour = calculatePixelsPerHour(timeRange, state.value.zoomLevel)
        val hoursDelta = offset.x.toDouble() / pixelsPerHour
        
        val newStartDate = dragStartTime!!.plus(Duration.ofHours(hoursDelta.toLong()))
        
        // Update temporary state
        _state.update { current ->
            current.copy(
                groups = current.groups.map { group ->
                    group.copy(
                        tasks = group.tasks.map { t ->
                            if (t.id == task.id) {
                                t.copy(
                                    startDate = newStartDate,
                                    left = calculateLeftPosition(newStartDate, timeRange)
                                )
                            } else t
                        }
                    )
                }
            )
        }
    }
    
    fun onTaskDragEnd(task: TimelineTaskDto, finalOffset: Offset) {
        if (dragStartTime == null) return
        
        val timeRange = state.value.timeRange
        val pixelsPerHour = calculatePixelsPerHour(timeRange, state.value.zoomLevel)
        val hoursDelta = finalOffset.x.toDouble() / pixelsPerHour
        
        val newStartDate = dragStartTime!!.plus(Duration.ofHours(hoursDelta.toLong()))
        
        // Call API to update task
        viewModelScope.launch {
            try {
                val response = timelineService.moveTask(
                    taskId = task.id,
                    newStartDate = newStartDate,
                    newDueDate = newStartDate.plus(task.durationMinutes, ChronoUnit.MINUTES),
                    checkConflicts = true
                )
                
                if (response.conflicts.isNotEmpty()) {
                    // Show conflict resolution dialog
                    _state.update { current ->
                        current.copy(
                            showConflictDialog = true,
                            currentConflict = response.conflicts.first()
                        )
                    }
                } else {
                    // Refresh timeline
                    loadTimeline()
                }
            } catch (e: Exception) {
                // Revert changes
                loadTimeline()
            }
        }
        
        // Reset drag state
        dragStartPosition = null
        dragStartTime = null
        draggedTask = null
    }
    
    private fun calculatePixelsPerHour(timeRange: TimeRangeDto, zoomLevel: ZoomLevel): Double {
        val totalHours = Duration.between(timeRange.start, timeRange.end).toHours().toDouble()
        val screenWidth = LocalConfiguration.current.screenWidthDp
        
        return when (zoomLevel) {
            ZoomLevel.HOUR -> screenWidth / 24.0
            ZoomLevel.DAY -> screenWidth / totalHours
            ZoomLevel.WEEK -> screenWidth / (totalHours / 24)
            else -> screenWidth / totalHours
        }
    }
}
```

## 📊 Последствия

### Положительные
- ✅ Интуитивная визуализация временных данных
- ✅ Обнаружение конфликтов в реальном времени
- ✅ Поддержка drag-and-drop
- ✅ Высокая производительность
- ✅ Масштабируемость

### Отрицательные
- ⚠️ Сложность реализации клиентской части
- ⚠️ Необходимость оптимизации запросов
- ⚠️ Сложность обработки больших объемов данных

### Нейтральные
- 🔹 Необходимость тестирования на разных устройствах
- 🔹 Поддержка разных платформ (Android, iOS, Web)

## 🔗 Связанные решения
- [ADR-001: Микросервисная архитектура](ADR-001-microservices.md) — Timeline Service
- [ADR-003: API дизайн](ADR-003-api-design.md) — API endpoints
- [ADR-004: Схема базы данных](ADR-004-database-schema.md) — Task model

## 📝 Примечания
- **Performance**: Использовать индексы для быстрых запросов по датам
- **Caching**: Кэшировать часто запрашиваемые диапазоны
- **Lazy Loading**: Подгружать задачи по мере скролла
- **Conflict Detection**: Обнаружение конфликтов на сервере и клиенте
- **Drag-and-Drop**: Поддержка на всех платформах

## ✅ Статус
- [ ] Proposed
- [ ] Under Discussion
- [x] Accepted
- [ ] Deprecated
- [ ] Replaced by [ADR-ZZZ]
