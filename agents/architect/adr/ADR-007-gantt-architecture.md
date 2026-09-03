# ADR-007: Архитектура Gantt Charts для Floktask

## 📅 Методология
- **Статус:** Accepted
- **Дата:** 2026-09-18
- **Автор:** Architect
- **Участники:** Product Manager, Backend, Frontend, UI/UX Designer

## 🎯 Контекст и проблема

### Контекст
Diagram Gantt — это стандартный инструмент для управления проектами, позволяющий визуализировать:
- Временные рамки задач и проектов
- Зависимости между задачами
- Прогресс выполнения
- Ресурсы и нагрузку
- Критический путь проекта

Для Floktask это критично для:
- Управления сложными проектами
- Визуализации зависимостей задач
- Планирования ресурсов
- Отслеживания прогресса
- Координации между командами

### Проблема
- ❌ Нет поддержки диаграмм Гантта
- ❌ Нет механизма отображения зависимостей
- ❌ Нет расчета критического пути
- ❌ Нет визуализации нагрузки ресурсов

### Драйверы (что нас мотивирует)
- **Управление проектами** — Профессиональный инструмент для сложных проектов
- **Визуализация** — Наглядное представление временных рамок
- **Коллаборация** — Координация работы между участниками
- **Аналитика** — Отслеживание прогресса и идентификация узких мест
- **Конкурентоспособность** — Аналогичные фичи есть в Jira, Asana, ClickUp

## ⚖️ Варианты решения

### Вариант 1: Client-Side Gantt Library
Использование готовой библиотеки для рендеринга на клиенте.

**Плюсы:**
- ✅ Быстрая реализация
- ✅ Хорошая производительность
- ✅ Готовые фичи (drag-and-drop, zoom, etc.)

**Минусы:**
- ❌ Зависимость от сторонней библиотеки
- ❌ Ограниченная кастомизация
- ❌ Проблемы с большими проектами

**Оценка:**
- **Сложность реализации:** Low
- **Стоимость:** $
- **Время:** 1 неделя
- **Риск:** Low

### Вариант 2: Server-Side Gantt Generation
Генерация диаграмм на сервере (SVG/PNG).

**Плюсы:**
- ✅ Полный контроль над рендерингом
- ✅ Нет проблем с производительностью на клиенте
- ✅ Поддержка экспорта в изображения

**Минусы:**
- ❌ Сложность реализации
- ❌ Ограниченная интерактивность
- ❌ Высокая нагрузка на сервер

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4 недели
- **Риск:** High

### Вариант 3: Hybrid Approach
Сервер предоставляет данные и частичный рендеринг, клиент завершает.

**Плюсы:**
- ✅ Оптимальная производительность
- ✅ Гибкость и интерактивность
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
**Hybrid Approach** — Сервер предоставляет оптимизированные данные, клиент рендерит с использованием кастомной реализации.

### Архитектура

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Gantt Chart Architecture                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Client Side (Mobile/Web)                        │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐  │   │
│  │  │  Gantt      │    │  Dependency │    │  Critical Path          │  │   │
│  │  │  Renderer   │    │  Resolver   │    │  Calculator            │  │   │
│  │  │  (Custom)   │    │  (Client)   │    │  (Client)              │  │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      API Gateway                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Gantt Service                                 │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐  │   │
│  │  │  Gantt      │    │  Critical   │    │  Resource              │  │   │
│  │  │  Data      │    │  Path       │    │  Allocation           │  │   │
│  │  │  Provider  │    │  Calculator │    │  Calculator           │  │   │
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
│  │  │  Gantt      │    │  Task       │    │  Dependencies           │ │   │
│  │  │  Cache     │    │  Database   │    │  Table                 │ │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Компоненты

#### 1. Gantt Service

**Ответственность:**
- Обработка запросов для диаграмм Гантта
- Расчет критического пути
- Расчет нагрузки ресурсов
- Оптимизация данных для клиента

**Endpoints:**
```
GET    /api/v1/gantt/projects/{projectId}    # Диаграмма Гантта для проекта
GET    /api/v1/gantt/critical-path            # Критический путь проекта
GET    /api/v1/gantt/resource-load            # Нагрузка ресурсов
POST   /api/v1/gantt/tasks/{taskId}/dependencies  # Установка зависимости
DELETE /api/v1/gantt/tasks/{taskId}/dependencies/{depId}  # Удаление зависимости
POST   /api/v1/gantt/baseline                # Создание базовой линии
```

#### 2. Gantt Data Provider

**Функционал:**
- Запрос задач проекта с зависимостями
- Фильтрация по датам, статусам, ресурсам
- Группировка по этапам проекта
- Расчет временных рамок

**SQL Пример:**
```sql
WITH task_dependencies AS (
    SELECT 
        t1.id as task_id,
        t2.id as depends_on_id,
        t2.title as depends_on_title,
        td.dependency_type,
        td.lag_minutes
    FROM tasks t1
    JOIN task_dependencies td ON t1.id = td.task_id
    JOIN tasks t2 ON td.depends_on_task_id = t2.id
    WHERE t1.project_id = ?
)
SELECT 
    t.id, t.uuid, t.title, t.description,
    t.start_date, t.due_date, t.duration_minutes,
    t.priority, t.status,
    t.assignee_id, u.username as assignee_name,
    td.task_id, td.depends_on_id, td.dependency_type, td.lag_minutes,
    depends_on.title as depends_on_title
FROM tasks t
LEFT JOIN task_dependencies td ON t.id = td.task_id
LEFT JOIN users u ON t.assignee_id = u.id
WHERE t.project_id = ?
ORDER BY t.start_date, td.dependency_type
```

#### 3. Critical Path Calculator

**Алгоритм расчета критического пути:**

```kotlin
class CriticalPathCalculator {
    
    data class TaskNode(
        val task: Task,
        val earlyStart: Instant,
        val earlyFinish: Instant,
        val lateStart: Instant,
        val lateFinish: Instant,
        val slack: Duration,
        val isCritical: Boolean
    )
    
    fun calculateCriticalPath(projectId: Long): List<TaskNode> {
        val tasks = taskService.getProjectTasksWithDependencies(projectId)
        
        // Forward pass - calculate early start/finish
        val forwardPass = forwardPass(tasks)
        
        // Backward pass - calculate late start/finish
        val backwardPass = backwardPass(tasks, forwardPass)
        
        // Calculate slack and identify critical path
        return tasks.map { task ->
            val earlyStart = forwardPass[task.id]?.earlyStart
            val earlyFinish = forwardPass[task.id]?.earlyFinish
            val lateStart = backwardPass[task.id]?.lateStart
            val lateFinish = backwardPass[task.id]?.lateFinish
            
            val slack = if (earlyStart != null && lateStart != null) {
                Duration.between(earlyStart, lateStart)
            } else Duration.ZERO
            
            TaskNode(
                task = task,
                earlyStart = earlyStart ?: task.startDate,
                earlyFinish = earlyFinish ?: task.startDate.plus(task.durationMinutes, ChronoUnit.MINUTES),
                lateStart = lateStart ?: task.dueDate?.minus(task.durationMinutes, ChronoUnit.MINUTES) ?: task.startDate,
                lateFinish = lateFinish ?: task.dueDate ?: task.startDate.plus(task.durationMinutes, ChronoUnit.MINUTES),
                slack = slack,
                isCritical = slack.isZero
            )
        }.sortedBy { it.earlyStart }
    }
    
    private fun forwardPass(tasks: List<Task>): Map<Long, TaskNode> {
        val result = mutableMapOf<Long, TaskNode>()
        val sortedTasks = tasks.sortedBy { it.startDate }
        
        sortedTasks.forEach { task ->
            val dependencies = task.dependencies.filter { dep ->
                sortedTasks.any { it.id == dep.dependsOnTaskId }
            }
            
            val earlyStart = if (dependencies.isEmpty()) {
                task.startDate
            } else {
                dependencies.maxOfOrNull { dep ->
                    val depTask = sortedTasks.find { it.id == dep.dependsOnTaskId }!!
                    val depFinish = result[depTask.id]?.earlyFinish ?: depTask.startDate.plus(depTask.durationMinutes, ChronoUnit.MINUTES)
                    depFinish.plus(dep.lagMinutes, ChronoUnit.MINUTES)
                } ?: task.startDate
            }
            
            val earlyFinish = earlyStart.plus(task.durationMinutes, ChronoUnit.MINUTES)
            
            result[task.id] = TaskNode(
                task = task,
                earlyStart = earlyStart,
                earlyFinish = earlyFinish,
                lateStart = earlyStart,  // Placeholder
                lateFinish = earlyFinish, // Placeholder
                slack = Duration.ZERO,    // Placeholder
                isCritical = false        // Placeholder
            )
        }
        
        return result
    }
    
    private fun backwardPass(tasks: List<Task>, forwardPass: Map<Long, TaskNode>): Map<Long, TaskNode> {
        val result = mutableMapOf<Long, TaskNode>()
        val sortedTasks = tasks.sortedByDescending { it.dueDate ?: it.startDate.plus(it.durationMinutes, ChronoUnit.MINUTES) }
        
        val projectEndDate = tasks.maxOfOrNull { it.dueDate ?: it.startDate.plus(it.durationMinutes, ChronoUnit.MINUTES) } ?: Instant.now()
        
        sortedTasks.forEach { task ->
            val dependents = tasks.filter { it.dependencies.any { dep -> dep.dependsOnTaskId == task.id } }
            
            val lateFinish = if (dependents.isEmpty()) {
                task.dueDate ?: projectEndDate
            } else {
                dependents.minOfOrNull { depTask ->
                    val depStart = result[depTask.id]?.lateStart ?: forwardPass[depTask.id]?.earlyStart ?: depTask.startDate
                    depStart.minus(depTask.dependencies.find { it.dependsOnTaskId == task.id }?.lagMinutes ?: 0, ChronoUnit.MINUTES)
                } ?: (task.dueDate ?: projectEndDate)
            }
            
            val lateStart = lateFinish.minus(task.durationMinutes, ChronoUnit.MINUTES)
            
            result[task.id] = TaskNode(
                task = task,
                earlyStart = forwardPass[task.id]?.earlyStart ?: task.startDate,
                earlyFinish = forwardPass[task.id]?.earlyFinish ?: task.startDate.plus(task.durationMinutes, ChronoUnit.MINUTES),
                lateStart = lateStart,
                lateFinish = lateFinish,
                slack = Duration.ZERO,    // Will be calculated later
                isCritical = false        // Will be calculated later
            )
        }
        
        return result
    }
}
```

#### 4. Resource Allocation Calculator

**Расчет нагрузки ресурсов:**

```kotlin
class ResourceAllocationCalculator {
    
    data class ResourceLoad(
        val resourceId: Long,
        val resourceName: String,
        val tasks: List<Task>,
        val totalHours: Double,
        val allocationPercentage: Double,
        val isOverloaded: Boolean
    )
    
    fun calculateResourceLoad(
        projectId: Long,
        timeRange: TimeRange
    ): List<ResourceLoad> {
        val tasks = taskService.getProjectTasksInRange(projectId, timeRange)
        
        // Group by assignee
        val tasksByResource = tasks.groupBy { it.assigneeId }
        
        return tasksByResource.map { (resourceId, resourceTasks) ->
            val user = userService.getUserById(resourceId)
            val totalHours = resourceTasks.sumOf { 
                it.durationMinutes?.toDouble()?.div(60) ?: 0.0
            }
            
            val availableHours = calculateAvailableHours(resourceId, timeRange)
            val allocationPercentage = (totalHours / availableHours) * 100
            
            ResourceLoad(
                resourceId = resourceId,
                resourceName = user?.username ?: "Unknown",
                tasks = resourceTasks,
                totalHours = totalHours,
                allocationPercentage = allocationPercentage,
                isOverloaded = allocationPercentage > 100
            )
        }.sortedByDescending { it.allocationPercentage }
    }
    
    private fun calculateAvailableHours(userId: Long, timeRange: TimeRange): Double {
        // Get user's working hours from settings
        val userSettings = userService.getUserSettings(userId)
        
        // Calculate available hours based on working schedule
        val totalDays = Duration.between(timeRange.start, timeRange.end).toDays()
        val workHoursPerDay = userSettings.workHoursPerDay ?: 8.0
        
        return totalDays.toDouble() * workHoursPerDay
    }
}
```

### Data Model

#### Gantt Task DTO

```kotlin
@Serializable
data class GanttTaskDto(
    val id: Long,
    val uuid: String,
    val title: String,
    val description: String? = null,
    
    // Time properties
    val startDate: Instant,
    val dueDate: Instant? = null,
    val durationMinutes: Int,
    
    // Progress
    val progressPercentage: Int = 0,
    val status: TaskStatus,
    
    // Assignment
    val assigneeId: Long? = null,
    val assigneeName: String? = null,
    val assigneeAvatar: String? = null,
    
    // Dependencies
    val dependencies: List<GanttDependencyDto> = emptyList(),
    val dependents: List<GanttDependencyDto> = emptyList(),
    
    // Critical path
    val isCritical: Boolean = false,
    val earlyStart: Instant? = null,
    val earlyFinish: Instant? = null,
    val lateStart: Instant? = null,
    val lateFinish: Instant? = null,
    val slack: Duration? = null,
    
    // Visual properties
    val color: String? = null,
    val projectId: Long? = null,
    val projectColor: String? = null,
    
    // Position (calculated on client)
    val left: Double = 0.0,
    val width: Double = 0.0,
    val top: Int = 0
)

@Serializable
data class GanttDependencyDto(
    val id: Long,
    val taskId: Long,
    val dependsOnTaskId: Long,
    val dependencyType: DependencyType,
    val lagMinutes: Int = 0
)

enum class DependencyType {
    FINISH_TO_START,   // Task A must finish before Task B can start
    START_TO_START,    // Task A must start before Task B can start
    FINISH_TO_FINISH, // Task A must finish before Task B can finish
    START_TO_FINISH    // Task A must start before Task B can finish
}
```

#### Gantt Chart DTO

```kotlin
@Serializable
data class GanttChartDto(
    val projectId: Long,
    val projectTitle: String,
    val timeRange: TimeRangeDto,
    val tasks: List<GanttTaskDto> = emptyList(),
    val criticalPath: List<GanttTaskDto> = emptyList(),
    val resourceLoad: List<ResourceLoadDto> = emptyList(),
    val milestones: List<GanttMilestoneDto> = emptyList(),
    val baselines: List<GanttBaselineDto> = emptyList(),
    val statistics: GanttStatisticsDto
)

@Serializable
data class TimeRangeDto(
    val start: Instant,
    val end: Instant
)

@Serializable
data class ResourceLoadDto(
    val resourceId: Long,
    val resourceName: String,
    val totalHours: Double,
    val allocationPercentage: Double,
    val isOverloaded: Boolean
)

@Serializable
data class GanttMilestoneDto(
    val id: Long,
    val title: String,
    val date: Instant,
    val color: String = "#FF5733",
    val isCompleted: Boolean = false
)

@Serializable
data class GanttBaselineDto(
    val id: Long,
    val name: String,
    val createdAt: Instant,
    val tasks: List<GanttBaselineTaskDto> = emptyList()
)

@Serializable
data class GanttBaselineTaskDto(
    val taskId: Long,
    val startDate: Instant,
    val dueDate: Instant,
    val durationMinutes: Int
)

@Serializable
data class GanttStatisticsDto(
    val totalTasks: Int,
    val completedTasks: Int,
    val inProgressTasks: Int,
    val totalDurationDays: Double,
    val projectStartDate: Instant,
    val projectEndDate: Instant,
    val criticalPathDurationDays: Double,
    val resourceUtilization: Double,
    val isOnSchedule: Boolean
)
```

### API Endpoints

#### GET /api/v1/gantt/projects/{projectId}

**Параметры:**
```
startDate: Instant (optional)
endDate: Instant (optional)
showDependencies: Boolean (optional, default: true)
showCriticalPath: Boolean (optional, default: true)
showResourceLoad: Boolean (optional, default: true)
zoomLevel: ZoomLevel (optional, default: WEEK)
```

**Ответ:**
```json
{
  "projectId": 123,
  "projectTitle": "Разработка мобильного приложения",
  "timeRange": {
    "start": "2026-09-01T00:00:00Z",
    "end": "2026-12-31T23:59:59Z"
  },
  "tasks": [...],
  "criticalPath": [...],
  "resourceLoad": [...],
  "milestones": [...],
  "baselines": [...],
  "statistics": {
    "totalTasks": 50,
    "completedTasks": 10,
    "inProgressTasks": 15,
    "totalDurationDays": 120,
    "projectStartDate": "2026-09-01T00:00:00Z",
    "projectEndDate": "2026-12-31T23:59:59Z",
    "criticalPathDurationDays": 90,
    "resourceUtilization": 75.5,
    "isOnSchedule": true
  }
}
```

#### GET /api/v1/gantt/critical-path/{projectId}

**Ответ:**
```json
{
  "projectId": 123,
  "criticalPath": [
    {
      "taskId": 1,
      "title": "Дизайн UI",
      "earlyStart": "2026-09-01T00:00:00Z",
      "earlyFinish": "2026-09-15T00:00:00Z",
      "lateStart": "2026-09-01T00:00:00Z",
      "lateFinish": "2026-09-15T00:00:00Z",
      "slack": 0,
      "isCritical": true
    },
    {
      "taskId": 2,
      "title": "Разработка backend",
      "earlyStart": "2026-09-15T00:00:00Z",
      "earlyFinish": "2026-10-30T00:00:00Z",
      "lateStart": "2026-09-15T00:00:00Z",
      "lateFinish": "2026-10-30T00:00:00Z",
      "slack": 0,
      "isCritical": true
    }
  ],
  "totalDurationDays": 45
}
```

#### POST /api/v1/gantt/tasks/{taskId}/dependencies

**Запрос:**
```json
{
  "dependsOnTaskId": 456,
  "dependencyType": "FINISH_TO_START",
  "lagMinutes": 0
}
```

**Ответ:**
```json
{
  "success": true,
  "dependency": {
    "id": 789,
    "taskId": 123,
    "dependsOnTaskId": 456,
    "dependencyType": "FINISH_TO_START",
    "lagMinutes": 0
  },
  "affectedTasks": [...],
  "warnings": []
}
```

### Клиентская часть

#### Gantt Chart Renderer (Compose)

```kotlin
@Composable
fun GanttChartScreen(
    viewModel: GanttViewModel,
    projectId: Long,
    onTaskClick: (Task) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header with controls
        GanttHeader(
            projectTitle = state.projectTitle,
            timeRange = state.timeRange,
            zoomLevel = state.zoomLevel,
            onZoomChange = viewModel::changeZoom,
            onTimeRangeChange = viewModel::changeTimeRange,
            onExport = viewModel::exportChart
        )
        
        // Resource load indicator
        if (state.showResourceLoad) {
            ResourceLoadBar(
                resourceLoad = state.resourceLoad,
                onResourceClick = viewModel::filterByResource
            )
        }
        
        // Gantt chart
        GanttChart(
            tasks = state.tasks,
            criticalPath = state.criticalPath,
            timeRange = state.timeRange,
            zoomLevel = state.zoomLevel,
            onTaskClick = onTaskClick,
            onTaskDrag = viewModel::onTaskDrag,
            onDependencyCreate = viewModel::createDependency
        )
        
        // Critical path legend
        if (state.showCriticalPath) {
            CriticalPathLegend(
                tasks = state.criticalPath,
                duration = state.statistics.criticalPathDurationDays
            )
        }
    }
}

@Composable
fun GanttChart(
    tasks: List<GanttTaskDto>,
    criticalPath: List<GanttTaskDto>,
    timeRange: TimeRangeDto,
    zoomLevel: ZoomLevel,
    onTaskClick: (GanttTaskDto) -> Unit,
    onTaskDrag: (GanttTaskDto, Offset) -> Unit,
    onDependencyCreate: (GanttTaskDto, GanttTaskDto) -> Unit
) {
    val scrollState = rememberScrollState()
    val timeScrollState = rememberScrollState()
    
    // Calculate task positions
    val taskPositions = remember(tasks, timeRange, zoomLevel) {
        tasks.map { task ->
            val startPos = calculatePosition(task.startDate, timeRange, zoomLevel)
            val endPos = calculatePosition(
                task.dueDate ?: task.startDate.plus(task.durationMinutes, ChronoUnit.MINUTES),
                timeRange,
                zoomLevel
            )
            
            GanttTaskPosition(
                task = task,
                left = startPos,
                width = endPos - startPos,
                top = calculateRow(task, tasks)
            )
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Time header
        GanttTimeHeader(
            timeRange = timeRange,
            zoomLevel = zoomLevel,
            scrollState = timeScrollState
        )
        
        // Task list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(taskPositions) { position ->
                GanttTaskRow(
                    position = position,
                    isCritical = criticalPath.any { it.id == position.task.id },
                    timeRange = timeRange,
                    timeScrollState = timeScrollState,
                    onClick = { onTaskClick(position.task) },
                    onDrag = { offset -> onTaskDrag(position.task, offset) }
                )
            }
        }
        
        // Dependency lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            taskPositions.forEach { position ->
                position.task.dependencies.forEach { dep ->
                    val depTaskPos = taskPositions.find { it.task.id == dep.dependsOnTaskId }
                    if (depTaskPos != null) {
                        drawDependencyLine(position, depTaskPos, dep.dependencyType)
                    }
                }
            }
        }
    }
}

@Composable
fun GanttTaskRow(
    position: GanttTaskPosition,
    isCritical: Boolean,
    timeRange: TimeRangeDto,
    timeScrollState: ScrollState,
    onClick: () -> Unit,
    onDrag: (Offset) -> Unit
) {
    val rowHeight = 40.dp
    
    Row(
        modifier = Modifier
            .height(rowHeight)
            .fillMaxWidth()
    ) {
        // Task info
        Text(
            text = position.task.title,
            modifier = Modifier
                .width(200.dp)
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyMedium
        )
        
        // Task bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight)
                .horizontalScroll(timeScrollState)
        ) {
            // Task bar
            Box(
                modifier = Modifier
                    .offset { IntOffset(position.left.roundToInt(), 0) }
                    .width(position.width.dp)
                    .height(24.dp)
                    .background(
                        color = if (isCritical) Color.Red else (position.task.color ?: Color.Blue),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = if (isCritical) 2.dp else 0.dp,
                        color = Color.Black,
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
            ) {
                // Progress indicator
                if (position.task.progressPercentage > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((position.width.dp * position.task.progressPercentage / 100))
                            .background(
                                color = Color.Green.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            
            // Milestone marker
            if (position.task.dependencies.isEmpty() && position.task.dependents.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(position.left.roundToInt() - 4, 0) }
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                )
            }
        }
    }
}

@Composable
fun drawDependencyLine(
    from: GanttTaskPosition,
    to: GanttTaskPosition,
    dependencyType: DependencyType
) {
    val startX = from.left + from.width
    val endX = to.left
    val y = from.top + 20
    
    when (dependencyType) {
        DependencyType.FINISH_TO_START -> {
            // Arrow from end of 'from' task to start of 'to' task
            drawLine(
                color = Color.Gray,
                start = Offset(startX, y),
                end = Offset(endX, to.top + 20),
                strokeWidth = 1.dp.toPx()
            )
            // Arrow head
            drawPath(
                path = Path().apply {
                    moveTo(endX, to.top + 20)
                    lineTo(endX - 4.dp.toPx(), to.top + 16)
                    moveTo(endX, to.top + 20)
                    lineTo(endX - 4.dp.toPx(), to.top + 24)
                },
                color = Color.Gray,
                style = Fill
            )
        }
        DependencyType.START_TO_START -> {
            drawLine(
                color = Color.Gray,
                start = Offset(from.left, y),
                end = Offset(endX, to.top + 20),
                strokeWidth = 1.dp.toPx()
            )
        }
        DependencyType.FINISH_TO_FINISH -> {
            drawLine(
                color = Color.Gray,
                start = Offset(startX, y),
                end = Offset(to.left + to.width, to.top + 20),
                strokeWidth = 1.dp.toPx()
            )
        }
        DependencyType.START_TO_FINISH -> {
            drawLine(
                color = Color.Gray,
                start = Offset(from.left, y),
                end = Offset(to.left + to.width, to.top + 20),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
```

### Drag-and-Drop для зависимостей

```kotlin
class GanttViewModel(
    private val ganttService: GanttService,
    private val taskService: TaskService
) : ViewModel() {
    
    private val _state = MutableStateFlow(GanttState())
    val state: StateFlow<GanttState> = _state.asStateFlow()
    
    private var dragStartTask: GanttTaskDto? = null
    private var dragEndTask: GanttTaskDto? = null
    
    fun onTaskDragStart(task: GanttTaskDto) {
        dragStartTask = task
    }
    
    fun onTaskDragEnd(task: GanttTaskDto) {
        if (dragStartTask != null && dragStartTask != task) {
            // Create dependency
            createDependency(dragStartTask!!, task)
        }
        dragStartTask = null
    }
    
    fun createDependency(from: GanttTaskDto, to: GanttTaskDto) {
        viewModelScope.launch {
            try {
                val response = ganttService.createDependency(
                    taskId = to.id,
                    dependsOnTaskId = from.id,
                    dependencyType = DependencyType.FINISH_TO_START,
                    lagMinutes = 0
                )
                
                if (response.warnings.isNotEmpty()) {
                    // Show warnings (e.g., circular dependency)
                    _state.update { current ->
                        current.copy(
                            warnings = response.warnings
                        )
                    }
                }
                
                // Refresh chart
                loadGanttChart()
            } catch (e: Exception) {
                // Show error
            }
        }
    }
    
    fun onTaskResize(task: GanttTaskDto, newDuration: Duration) {
        viewModelScope.launch {
            try {
                taskService.updateTask(
                    id = task.id,
                    durationMinutes = newDuration.toMinutes().toInt()
                )
                loadGanttChart()
            } catch (e: Exception) {
                // Revert changes
            }
        }
    }
    
    private fun calculatePosition(date: Instant, timeRange: TimeRangeDto, zoomLevel: ZoomLevel): Float {
        val totalDuration = Duration.between(timeRange.start, timeRange.end).toMinutes().toDouble()
        val dateDuration = Duration.between(timeRange.start, date).toMinutes().toDouble()
        
        val screenWidth = LocalConfiguration.current.screenWidthDp
        
        return when (zoomLevel) {
            ZoomLevel.DAY -> (dateDuration / totalDuration * screenWidth).toFloat()
            ZoomLevel.WEEK -> (dateDuration / (totalDuration / 7) * screenWidth / 7).toFloat()
            ZoomLevel.MONTH -> (dateDuration / (totalDuration / 30) * screenWidth / 30).toFloat()
            else -> (dateDuration / totalDuration * screenWidth).toFloat()
        }
    }
    
    private fun calculateRow(task: GanttTaskDto, allTasks: List<GanttTaskDto>): Int {
        // Simple row calculation based on dependencies
        // In a real implementation, use a proper layout algorithm
        val tasksInSameTime = allTasks.filter { other ->
            other.id != task.id &&
            other.startDate.isBefore(task.dueDate) &&
            other.dueDate?.isAfter(task.startDate) == true
        }
        
        return tasksInSameTime.size
    }
}
```

## 📊 Последствия

### Положительные
- ✅ Профессиональный инструмент для управления проектами
- ✅ Визуализация зависимостей и критического пути
- ✅ Расчет нагрузки ресурсов
- ✅ Интерактивный интерфейс
- ✅ Поддержка drag-and-drop

### Отрицательные
- ⚠️ Сложность реализации
- ⚠️ Высокая нагрузка на клиент при большом количестве задач
- ⚠️ Сложность расчета критического пути

### Нейтральные
- 🔹 Необходимость оптимизации для мобильных устройств
- 🔹 Поддержка разных платформ

## 🔗 Связанные решения
- [ADR-001: Микросервисная архитектура](ADR-001-microservices.md) — Gantt Service
- [ADR-003: API дизайн](ADR-003-api-design.md) — API endpoints
- [ADR-004: Схема базы данных](ADR-004-database-schema.md) — Task dependencies
- [ADR-006: Архитектура Timeline View](ADR-006-timeline-architecture.md) — Похожие паттерны

## 📝 Примечания
- **Performance**: Использовать индексы для быстрых запросов зависимостей
- **Caching**: Кэшировать данные диаграмм Гантта
- **Lazy Loading**: Подгружать задачи по мере необходимости
- **Critical Path**: Пересчитывать при изменении зависимостей
- **Export**: Поддержка экспорта в PNG, PDF, Excel

## ✅ Статус
- [ ] Proposed
- [ ] Under Discussion
- [x] Accepted
- [ ] Deprecated
- [ ] Replaced by [ADR-ZZZ]
