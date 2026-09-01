# ADR-008: Кроссплатформенная Архитектура (Kotlin Multiplatform)

## 📅 Методология
- **Статус:** Accepted
- **Дата:** 2026-09-20
- **Автор:** Architect
- **Участники:** Product Manager, Backend, Frontend, Mobile Developers

## 🎯 Контекст и проблема

### Контекст
Floktask изначально разработан как Android-приложение на Kotlin. Для расширения на другие платформы (iOS, Web, Desktop) требуется кроссплатформенная архитектура.

**Целевые платформы:**
- **Android** (текущая) - Kotlin + Jetpack Compose
- **iOS** - Swift + SwiftUI (цель)
- **Web** - TypeScript + React (цель)
- **Desktop** - Kotlin + Compose for Desktop (опционально)

### Проблема
- ❌ Нет поддержки iOS
- ❌ Нет веб-версии
- ❌ Дублирование кода между платформами
- ❌ Разные команды для разных платформ
- ❌ Сложность синхронизации фич

### Драйверы (что нас мотивирует)
- **Кроссплатформенность** — Один код для всех платформ
- **Экономия ресурсов** — Меньше дублирования кода
- **Синхронность** — Одинаковые фичи на всех платформах
- **Скорость разработки** — Быстрее выпуск новых фич
- **Конкурентоспособность** — Todoist и TickTick поддерживают все платформы

## ⚖️ Варианты решения

### Вариант 1: Native Development
Разрабатывать нативные приложения для каждой платформы.

**Плюсы:**
- ✅ Максимальная производительность
- ✅ Полный доступ к платформенным фичам
- ✅ Нативный UX/UI

**Минусы:**
- ❌ Дублирование кода (80%+)
- ❌ Разные команды для разных платформ
- ❌ Сложность синхронизации фич
- ❌ Высокая стоимость разработки

**Оценка:**
- **Сложность реализации:** Very High
- **Стоимость:** $$$$$
- **Время:** 6+ месяцев
- **Риск:** High

### Вариант 2: Flutter
Использовать Flutter для кроссплатформенной разработки.

**Плюсы:**
- ✅ Один код для всех платформ
- ✅ Быстрая разработка
- ✅ Хороший UX/UI

**Минусы:**
- ❌ Сложность интеграции с существующим Kotlin кодом
- ❌ Проблемы с производительностью
- ❌ Ограниченный доступ к нативным API
- ❌ Не совместим с Jetpack Compose

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4-6 месяцев
- **Риск:** Medium

### Вариант 3: React Native
Использовать React Native для мобильных платформ.

**Плюсы:**
- ✅ Один код для Android и iOS
- ✅ Большое сообщество
- ✅ Много готовых библиотек

**Минусы:**
- ❌ Сложность интеграции с Kotlin
- ❌ Проблемы с производительностью
- ❌ Ограниченный доступ к нативным API
- ❌ Не совместим с Jetpack Compose

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4-6 месяцев
- **Риск:** Medium

### Вариант 4: Kotlin Multiplatform (KMP)
Использовать Kotlin Multiplatform для общего кода + нативные UI.

**Плюсы:**
- ✅ Общий код для бизнес-логики
- ✅ Интеграция с существующим Kotlin кодом
- ✅ Нативный UI для каждой платформы
- ✅ Полный доступ к платформенным API
- ✅ Постепенная миграция

**Минусы:**
- ❌ Сложность настройки
- ❌ Разные UI для разных платформ
- ❌ Не все библиотеки поддерживают KMP

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 3-4 месяца
- **Риск:** Medium

### Вариант 5: Hybrid (KMP + Compose Multiplatform)
Использовать KMP для бизнес-логики + Compose Multiplatform для UI.

**Плюсы:**
- ✅ Максимальное повторное использование кода
- ✅ Единый UI для всех платформ
- ✅ Интеграция с существующим кодом
- ✅ Хорошая производительность

**Минусы:**
- ❌ Compose Multiplatform еще не стабилен для iOS
- ❌ Ограниченный доступ к нативным API
- ❌ Сложность настройки

**Оценка:**
- **Сложность реализации:** High
- **Стоимость:** $$$
- **Время:** 4-5 месяцев
- **Риск:** High

## 🎯 Выбранное решение
**Kotlin Multiplatform (KMP) + Platform-Specific UI**

### Архитектура

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Kotlin Multiplatform Architecture                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Shared Module (KMP)                           │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Common Code (100%)                          │ │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │ │   │
│  │  │  │   Domain    │  │  Data       │  │  Use Cases              │  │ │   │
│  │  │  │   Layer    │  │  Layer     │  │                         │  │ │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────────────────┘  │ │   │
│  │  │                                                                   │ │   │
│  │  │  ┌───────────────────────────────────────────────────────────┐ │ │   │
│  │  │  │                    Platform-Specific Code                    │ │ │   │
│  │  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │ │ │   │
│  │  │  │  │  Android    │  │    iOS      │  │      Web           │ │ │ │   │
│  │  │  │  │  (Kotlin)   │  │  (Kotlin)   │  │  (Kotlin/JS)        │ │ │ │   │
│  │  │  │  └─────────────┘  └─────────────┘  └─────────────────────┘ │ │ │   │
│  │  │  └───────────────────────────────────────────────────────────┘ │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│         ┌──────────────────────────┬──────────────────────────┐         │
│         ▼                          ▼                          ▼                │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐      │
│  │  Android    │           │    iOS      │           │     Web     │      │
│  │  Jetpack   │           │  SwiftUI   │           │   React     │      │
│  │  Compose   │           │             │           │             │      │
│  └─────────────┘           └─────────────┘           └─────────────┘      │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Backend Services (Kotlin/Spring)                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Структура проекта

```
floktask-multiplatform/
├── build.gradle.kts                    # Root build file
├── settings.gradle.kts                 # Settings
│
├── gradle/
│   └── plugins/                        # Custom plugins
│
├── shared/                             # Shared module (KMP)
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── commonMain/                 # Common code for all platforms
│   │   │   ├── kotlin/                 # Kotlin source
│   │   │   │   ├── com/floktask/
│   │   │   │   │   ├── domain/       # Domain layer
│   │   │   │   │   │   ├── model/     # Data models
│   │   │   │   │   │   ├── repository/ # Repository interfaces
│   │   │   │   │   │   └── usecase/   # Use cases
│   │   │   │   │   ├── data/         # Data layer
│   │   │   │   │   │   ├── repository/ # Repository implementations
│   │   │   │   │   │   ├── mapper/    # Mappers
│   │   │   │   │   │   └── source/    # Data sources
│   │   │   │   │   ├── network/      # Network layer
│   │   │   │   │   │   ├── api/       # API interfaces
│   │   │   │   │   │   ├── dto/       # DTOs
│   │   │   │   │   │   └── service/   # Services
│   │   │   │   │   └── utils/        # Utilities
│   │   │   │   └── resources/        # Resources
│   │   │   │
│   │   ├── commonTest/                 # Common tests
│   │   │   └── kotlin/
│   │   │
│   │   ├── androidMain/                # Android-specific code
│   │   │   └── kotlin/
│   │   │
│   │   ├── iosMain/                    # iOS-specific code
│   │   │   └── kotlin/
│   │   │
│   │   ├── iosTest/                    # iOS-specific tests
│   │   │   └── kotlin/
│   │   │
│   │   ├── jvmMain/                    # JVM-specific code
│   │   │   └── kotlin/
│   │   │
│   │   └── jsMain/                     # JS-specific code
│   │       └── kotlin/
│   │
│   └── build/
│
├── android/                            # Android app
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── res/
│   │   │   └── kotlin/
│   │   │       └── com/floktask/
│   │   │           ├── presentation/   # Presentation layer
│   │   │           │   ├── ui/        # UI components
│   │   │           │   ├── viewmodel/ # ViewModels
│   │   │           │   └── navigation/ # Navigation
│   │   │           └── MainActivity.kt
│   │   └── test/
│   └── build/
│
├── ios/                                # iOS app (future)
│   ├── Podfile
│   ├── Info.plist
│   ├── Sources/
│   │   └── App/
│   │       ├── AppDelegate.swift
│   │       ├── SceneDelegate.swift
│   │       └── Views/
│   └── Resources/
│
├── web/                                # Web app (future)
│   ├── build.gradle.kts
│   ├── src/
│   │   └── main/
│   │       ├── kotlin/
│   │       └── resources/
│   └── build/
│
└── backend/                            # Backend services
    ├── api-gateway/
    ├── user-service/
    ├── task-service/
    └── ...
```

### Shared Module Structure

```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.22"
    `kotlinx-serialization`
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    ios {
        binaries {
            framework {
                baseName = "FloktaskShared"
            }
        }
    }
    
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "60000"
                }
            }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin
                implementation(kotlinx.coroutines.core)
                implementation(kotlinx.serialization.core)
                implementation(kotlinx.serialization.json)
                implementation(kotlinx.datetime)
                
                // Multiplatform
                implementation("com.russhwolf:multiplatform-settings:1.1.1")
                implementation("com.russhwolf:multiplatform-settings-serialization:1.1.1")
                
                // Network
                implementation("io.ktor:ktor-client-core:2.3.7")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
                implementation("io.ktor:ktor-client-logging:2.3.7")
                
                // SQLDelight for local DB
                implementation("com.squareup.sqldelight:runtime:2.0.1")
                implementation("com.squareup.sqldelight:coroutines-extensions:2.0.1")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlinx.coroutines.test)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime-test:0.4.0")
                implementation("io.ktor:ktor-client-mock:2.3.7")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:2.3.7")
                implementation("io.ktor:ktor-client-okhttp:2.3.7")
                implementation("com.squareup.sqldelight:android-driver:2.0.1")
            }
        }
        
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:2.3.7")
                implementation("com.squareup.sqldelight:native-driver:2.0.1")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:2.3.7")
                implementation("com.squareup.sqldelight:sqlite-driver:2.0.1")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:2.3.7")
                implementation("com.squareup.sqldelight:sqljs-driver:2.0.1")
            }
        }
    }
}
```

### Platform-Specific Implementation

#### Android

```kotlin
// android/src/main/kotlin/com/floktask/FloktaskApp.kt
class FloktaskApp : Application() {
    
    val sharedModule: FloktaskShared by lazy {
        FloktaskShared(
            platform = Platform.Android,
            context = this
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        sharedModule.init()
    }
}

// android/src/main/kotlin/com/floktask/presentation/MainActivity.kt
class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as FloktaskApp
        val sharedModule = app.sharedModule
        
        // Initialize with platform-specific dependencies
        viewModel.init(sharedModule.taskRepository, sharedModule.projectRepository)
        
        setContent {
            FloktaskTheme {
                AppContent(viewModel)
            }
        }
    }
}
```

#### iOS (Swift)

```swift
// ios/Sources/App/AppDelegate.swift
import UIKit
import FloktaskShared

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var sharedModule: FloktaskShared?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        // Initialize shared module
        sharedModule = FloktaskShared(
            platform: Platform.Ios,
            context: nil
        )
        sharedModule?.init()
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = ViewController(sharedModule: sharedModule!)
        window?.makeKeyAndVisible()
        
        return true
    }
}

// ios/Sources/App/ViewController.swift
import UIKit
import SwiftUI
import FloktaskShared

class ViewController: UIHostingController<ContentView> {
    
    init(sharedModule: FloktaskShared) {
        super.init(rootView: ContentView(viewModel: TaskViewModel(sharedModule: sharedModule)))
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
```

#### Web (Kotlin/JS + React)

```kotlin
// web/src/main/kotlin/com/floktask/main.kt
import react.dom.render
import react.router.dom.HashRouter
import kotlinx.browser.document
import com.floktask.presentation.App

fun main() {
    val sharedModule = FloktaskShared(
        platform = Platform.Web,
        context = null
    )
    sharedModule.init()
    
    render(HashRouter::class) {
        App(sharedModule)
    }.apply {
        document.getElementById("root")?.let { mount(it) }
    }
}
```

### Общий код (Shared Module)

#### Domain Layer

```kotlin
// shared/src/commonMain/kotlin/com/floktask/domain/model/Task.kt
expect class Instant() {
    fun toEpochMilliseconds(): Long
    fun plus(millis: Long): Instant
    fun minus(millis: Long): Instant
    fun isBefore(other: Instant): Boolean
    fun isAfter(other: Instant): Boolean
    
    companion object {
        fun parse(isoString: String): Instant
        fun now(): Instant
    }
}

@Serializable
data class Task(
    val id: Long? = null,
    val uuid: String,
    val title: String,
    val description: String? = null,
    val status: TaskStatus,
    val priority: TaskPriority,
    val projectId: Long? = null,
    val userId: Long,
    val startDate: Instant? = null,
    val dueDate: Instant? = null,
    val durationMinutes: Int? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    val version: Int = 1
)

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE, ARCHIVED, DELETED
}

enum class TaskPriority {
    NONE, LOW, MEDIUM, HIGH
}
```

#### Data Layer

```kotlin
// shared/src/commonMain/kotlin/com/floktask/data/repository/TaskRepository.kt
interface TaskRepository {
    suspend fun getAllTasks(userId: Long): List<Task>
    suspend fun getTaskById(id: Long): Task?
    suspend fun createTask(task: Task): Task
    suspend fun updateTask(task: Task): Task
    suspend fun deleteTask(id: Long): Boolean
    suspend fun getTasksByProject(projectId: Long): List<Task>
    suspend fun getTasksByDateRange(startDate: Instant, endDate: Instant): List<Task>
    suspend fun syncTasks(changes: List<TaskChange>): SyncResult
}

// shared/src/commonMain/kotlin/com/floktask/data/repository/TaskRepositoryImpl.kt
class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val conflictResolver: ConflictResolver
) : TaskRepository {
    
    override suspend fun getAllTasks(userId: Long): List<Task> {
        return try {
            // Try to get from remote first
            val remoteTasks = remoteDataSource.getAllTasks(userId)
            localDataSource.saveAll(remoteTasks)
            remoteTasks
        } catch (e: Exception) {
            // Fallback to local
            localDataSource.getAllTasks(userId)
        }
    }
    
    override suspend fun createTask(task: Task): Task {
        val localTask = localDataSource.createTask(task)
        
        return try {
            val remoteTask = remoteDataSource.createTask(task)
            localDataSource.saveTask(remoteTask)
            remoteTask
        } catch (e: Exception) {
            localTask
        }
    }
    
    override suspend fun updateTask(task: Task): Task {
        val localTask = localDataSource.updateTask(task)
        
        return try {
            val remoteTask = remoteDataSource.updateTask(task)
            localDataSource.saveTask(remoteTask)
            remoteTask
        } catch (e: Exception) {
            localTask
        }
    }
    
    override suspend fun syncTasks(changes: List<TaskChange>): SyncResult {
        return try {
            val result = remoteDataSource.syncTasks(changes)
            
            // Apply changes locally
            result.applied.forEach { task ->
                localDataSource.saveTask(task)
            }
            
            // Handle conflicts
            result.conflicts.forEach { conflict ->
                val resolved = conflictResolver.resolve(conflict)
                if (resolved != null) {
                    localDataSource.saveTask(resolved)
                }
            }
            
            SyncResult(
                applied = result.applied,
                conflicts = result.conflicts,
                version = result.version
            )
        } catch (e: Exception) {
            SyncResult(
                applied = emptyList(),
                conflicts = emptyList(),
                version = -1,
                error = e.message
            )
        }
    }
}
```

#### Network Layer

```kotlin
// shared/src/commonMain/kotlin/com/floktask/network/api/TaskApi.kt
interface TaskApi {
    suspend fun getAllTasks(userId: Long): List<TaskDto>
    suspend fun getTaskById(id: Long): TaskDto
    suspend fun createTask(task: TaskCreateRequest): TaskDto
    suspend fun updateTask(id: Long, task: TaskUpdateRequest): TaskDto
    suspend fun deleteTask(id: Long): Boolean
}

// shared/src/commonMain/kotlin/com/floktask/network/FloktaskApiClient.kt
expect class FloktaskApiClient() {
    val taskApi: TaskApi
    val projectApi: ProjectApi
    val userApi: UserApi
    val syncApi: SyncApi
    
    companion object {
        fun create(baseUrl: String, token: String? = null): FloktaskApiClient
    }
}

// shared/src/androidMain/kotlin/com/floktask/network/FloktaskApiClient.android.kt
actual class FloktaskApiClient actual constructor() {
    actual val taskApi: TaskApi = TaskApiImpl()
    actual val projectApi: ProjectApi = ProjectApiImpl()
    actual val userApi: UserApi = UserApiImpl()
    actual val syncApi: SyncApi = SyncApiImpl()
    
    companion object {
        actual fun create(baseUrl: String, token: String?): FloktaskApiClient {
            return FloktaskApiClient()
        }
    }
}

// shared/src/iosMain/kotlin/com/floktask/network/FloktaskApiClient.ios.kt
actual class FloktaskApiClient actual constructor() {
    actual val taskApi: TaskApi = TaskApiImpl()
    actual val projectApi: ProjectApi = ProjectApiImpl()
    actual val userApi: UserApi = UserApiImpl()
    actual val syncApi: SyncApi = SyncApiImpl()
    
    companion object {
        actual fun create(baseUrl: String, token: String?): FloktaskApiClient {
            return FloktaskApiClient()
        }
    }
}
```

### Platform-Specific Implementation

#### Android Network Implementation

```kotlin
// shared/src/androidMain/kotlin/com/floktask/network/TaskApiImpl.android.kt
class TaskApiImpl : TaskApi {
    
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                serializeCompilerGeneratedSubclassDiscriminator = false
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(Auth) {
            bearer {
                loadTokens { TokenManager.getToken() }
            }
        }
    }
    
    override suspend fun getAllTasks(userId: Long): List<TaskDto> {
        return client.get("${BuildKonfig.BASE_URL}/api/v1/tasks") {
            parameter("userId", userId)
        }.body()
    }
    
    override suspend fun createTask(task: TaskCreateRequest): TaskDto {
        return client.post("${BuildKonfig.BASE_URL}/api/v1/tasks") {
            contentType(ContentType.Application.Json)
            setBody(task)
        }.body()
    }
}
```

#### iOS Network Implementation

```kotlin
// shared/src/iosMain/kotlin/com/floktask/network/TaskApiImpl.ios.kt
class TaskApiImpl : TaskApi {
    
    private val client = HttpClient(Ios) {
        install(ContentNegotiation) {
            json(Json {
                serializeCompilerGeneratedSubclassDiscriminator = false
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(Auth) {
            bearer {
                loadTokens { TokenManager.getToken() }
            }
        }
        
        engine {
            pipelines.build { install(IosCookiesStorage) }
        }
    }
    
    override suspend fun getAllTasks(userId: Long): List<TaskDto> {
        return client.get("${BuildKonfig.BASE_URL}/api/v1/tasks") {
            parameter("userId", userId)
        }.body()
    }
}
```

### Local Database (SQLDelight)

```kotlin
// shared/src/commonMain/kotlin/com/floktask/data/source/local/Database.kt
@Serializable
data class TaskEntity(
    val id: Long? = null,
    val uuid: String,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    val projectId: Long? = null,
    val userId: Long,
    val startDate: String? = null,
    val dueDate: String? = null,
    val durationMinutes: Long? = null,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null,
    val version: Long
)

// shared/src/commonMain/kotlin/com/floktask/data/source/local/FloktaskDatabase.kt
@Database
interface FloktaskDatabase {
    fun taskQueries(): TaskQueries
    fun projectQueries(): ProjectQueries
    fun userQueries(): UserQueries
}

val database: FloktaskDatabase = expect fun (driver: SqlDriver): FloktaskDatabase

// shared/src/androidMain/kotlin/com/floktask/data/source/local/FloktaskDatabase.android.kt
actual fun database(driver: SqlDriver): FloktaskDatabase {
    return FloktaskDatabase(driver)
}

// shared/src/iosMain/kotlin/com/floktask/data/source/local/FloktaskDatabase.ios.kt
actual fun database(driver: SqlDriver): FloktaskDatabase {
    return FloktaskDatabase(driver)
}

// shared/src/jvmMain/kotlin/com/floktask/data/source/local/FloktaskDatabase.jvm.kt
actual fun database(driver: SqlDriver): FloktaskDatabase {
    return FloktaskDatabase(driver)
}

// shared/src/jsMain/kotlin/com/floktask/data/source/local/FloktaskDatabase.js.kt
actual fun database(driver: SqlDriver): FloktaskDatabase {
    return FloktaskDatabase(driver)
}
```

### Dependency Injection

```kotlin
// shared/src/commonMain/kotlin/com/floktask/di/SharedModule.kt
expect class SharedModule(platform: Platform) {
    val taskRepository: TaskRepository
    val projectRepository: ProjectRepository
    val userRepository: UserRepository
    val syncService: SyncService
    val apiClient: FloktaskApiClient
    
    fun init()
}

// shared/src/commonMain/kotlin/com/floktask/di/Platform.kt
enum class Platform {
    Android, Ios, Jvm, Js, Web
}

// shared/src/androidMain/kotlin/com/floktask/di/SharedModule.android.kt
actual class SharedModule actual constructor(platform: Platform) {
    
    private val database: FloktaskDatabase by lazy {
        val driver = AndroidSqliteDriver(
            schema = FloktaskDatabase.Schema,
            context = context,
            name = "floktask.db"
        )
        FloktaskDatabase(driver)
    }
    
    private val tokenManager: TokenManager by lazy { AndroidTokenManager(context) }
    
    actual val apiClient: FloktaskApiClient by lazy {
        FloktaskApiClient.create(
            baseUrl = BuildKonfig.BASE_URL,
            token = tokenManager.getToken()
        )
    }
    
    actual val taskRepository: TaskRepository by lazy {
        val local = TaskLocalDataSource(database.taskQueries())
        val remote = TaskRemoteDataSource(apiClient.taskApi)
        TaskRepositoryImpl(local, remote, ConflictResolverImpl())
    }
    
    actual val projectRepository: ProjectRepository by lazy {
        val local = ProjectLocalDataSource(database.projectQueries())
        val remote = ProjectRemoteDataSource(apiClient.projectApi)
        ProjectRepositoryImpl(local, remote)
    }
    
    actual val userRepository: UserRepository by lazy {
        val local = UserLocalDataSource(database.userQueries())
        val remote = UserRemoteDataSource(apiClient.userApi)
        UserRepositoryImpl(local, remote)
    }
    
    actual val syncService: SyncService by lazy {
        SyncServiceImpl(taskRepository, projectRepository, userRepository)
    }
    
    actual fun init() {
        // Initialize database
        database.taskQueries().createTables()
        database.projectQueries().createTables()
        database.userQueries().createTables()
    }
}
```

### Build Configuration

```kotlin
// build.gradle.kts (root)
plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.compose") version "1.5.3" apply false
}

// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "floktask-multiplatform"
include(":shared")
include(":android")
include(":ios")
include(":web")
```

## 📊 Последствия

### Положительные
- ✅ Общий код для всех платформ (80%+)
- ✅ Нативный UI для каждой платформы
- ✅ Интеграция с существующим Kotlin кодом
- ✅ Постепенная миграция
- ✅ Полный доступ к платформенным API

### Отрицательные
- ⚠️ Сложность настройки
- ⚠️ Разные UI для разных платформ
- ⚠️ Не все библиотеки поддерживают KMP
- ⚠️ Сложность отладки на iOS

### Нейтральные
- 🔹 Необходимость обучения команды
- 🔹 Поддержка разных IDE (Android Studio, Xcode, IntelliJ)

## 🔗 Связанные решения
- [ADR-001: Микросервисная архитектура](ADR-001-microservices.md) — Backend services
- [ADR-002: Offline-first подход](ADR-002-offline-first.md) — Sync strategy
- [ADR-003: API дизайн](ADR-003-api-design.md) — API contracts
- [ADR-004: Схема базы данных](ADR-004-database-schema.md) — Data models

## 📝 Примечания
- **Migration Strategy**: Постепенная миграция модулей
- **Testing**: Unit тесты для общего кода, UI тесты для платформенного
- **CI/CD**: Отдельные пайплайны для каждой платформы
- **Dependencies**: Использовать multiplatform-совместимые библиотеки
- **Performance**: Мониторить производительность на всех платформах

## ✅ Статус
- [ ] Proposed
- [ ] Under Discussion
- [x] Accepted
- [ ] Deprecated
- [ ] Replaced by [ADR-ZZZ]
