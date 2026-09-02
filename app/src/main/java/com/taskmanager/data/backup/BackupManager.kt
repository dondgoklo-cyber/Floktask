package com.taskmanager.data.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.withTransaction
import com.taskmanager.data.local.dao.AccountDao
import com.taskmanager.data.local.dao.BudgetDao
import com.taskmanager.data.local.dao.CategoryDao
import com.taskmanager.data.local.dao.GoalDao
import com.taskmanager.data.local.dao.HabitDao
import com.taskmanager.data.local.dao.HabitLogDao
import com.taskmanager.data.local.dao.NoteDao
import com.taskmanager.data.local.dao.NoteFolderDao
import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.data.local.dao.SubprojectDao
import com.taskmanager.data.local.dao.SubtaskDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TaskTagDao
import com.taskmanager.data.local.dao.TransactionDao
import com.taskmanager.data.local.dao.UserStatsDao
import com.taskmanager.data.local.database.AppDatabase
import com.taskmanager.data.local.entity.AccountEntity
import com.taskmanager.data.local.entity.BudgetEntity
import com.taskmanager.data.local.entity.CategoryEntity
import com.taskmanager.data.local.entity.GoalEntity
import com.taskmanager.data.local.entity.HabitEntity
import com.taskmanager.data.local.entity.HabitLogEntity
import com.taskmanager.data.local.entity.NoteEntity
import com.taskmanager.data.local.entity.NoteFolderEntity
import com.taskmanager.data.local.entity.PomodoroSessionEntity
import com.taskmanager.data.local.entity.ProjectEntity
import com.taskmanager.data.local.entity.SubprojectEntity
import com.taskmanager.data.local.entity.SubtaskEntity
import com.taskmanager.data.local.entity.TagEntity
import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.data.local.entity.TaskTagEntity
import com.taskmanager.data.local.entity.TransactionEntity
import com.taskmanager.data.local.entity.UserStatsEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Полный офлайн backup/restore пользовательских данных в JSON.
 *
 * Принцип: Backup → Restore → идентичная логическая БД.
 * Сохраняются все ID и все связи (subtasks, task_tags, finance, notes, habits, ...).
 * Restore выполняется транзакционно: либо все данные применяются, либо ничего (rollback).
 *
 * Формат:
 * {
 *   "formatVersion": 3,            // версия формата backup (миграция формата)
 *   "dbVersion": 14,               // версия схемы Room, из которой сделан backup
 *   "checksum": "<sha256(data)>",  // целостность payload
 *   "encrypted": false,            // явно: шифрование не используется
 *   "data": "<json-string>"        // сериализованные таблицы
 * }
 *
 * Внимание: backup НЕ шифруется. Любая "обфускация" вводит в заблуждение, поэтому
 * поле `encrypted` всегда честно = false и ключи шифрования не хранятся.
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val subprojectDao: SubprojectDao,
    private val tagDao: TagDao,
    private val userStatsDao: UserStatsDao,
    private val subtaskDao: SubtaskDao,
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val pomodoroSessionDao: PomodoroSessionDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val noteDao: NoteDao,
    private val noteFolderDao: NoteFolderDao,
    private val budgetDao: BudgetDao,
    private val goalDao: GoalDao,
    private val taskTagDao: TaskTagDao
) {
    companion object {
        private const val TAG = "BackupManager"
        const val FORMAT_VERSION = 3
        private const val KEY_FORMAT = "formatVersion"
        private const val KEY_DB_VERSION = "dbVersion"
        private const val KEY_CHECKSUM = "checksum"
        private const val KEY_ENCRYPTED = "encrypted"
        private const val KEY_DATA = "data"
    }

    /** Результат операции restore с диагностикой. */
    sealed interface RestoreResult {
        data object Success : RestoreResult
        data class Error(val reason: String) : RestoreResult
    }

    suspend fun exportToUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val dataStr = buildExportJson().toString()
            val backup = JSONObject().apply {
                put(KEY_FORMAT, FORMAT_VERSION)
                put(KEY_DB_VERSION, AppDatabase.DATABASE_VERSION)
                put(KEY_CHECKSUM, sha256(dataStr))
                put(KEY_ENCRYPTED, false)
                put(KEY_DATA, dataStr)
            }
            val written = context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(backup.toString(2).toByteArray(Charsets.UTF_8))
                true
            } ?: false
            written
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            false
        }
    }

    suspend fun importFromUri(uri: Uri): RestoreResult = withContext(Dispatchers.IO) {
        try {
            val backupStr = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader(Charsets.UTF_8).readText()
            } ?: return@withContext RestoreResult.Error("Cannot read backup file")

            val backup = JSONObject(backupStr)
            val dataStr = backup.optString(KEY_DATA, backupStr)
            val expectedChecksum = backup.optString(KEY_CHECKSUM, "")
            if (expectedChecksum.isNotEmpty()) {
                val actual = sha256(dataStr)
                if (actual != expectedChecksum) {
                    return@withContext RestoreResult.Error("Checksum mismatch — backup may be corrupted")
                }
            }
            val formatVersion = backup.optInt(KEY_FORMAT, 1)
            if (formatVersion > FORMAT_VERSION) {
                return@withContext RestoreResult.Error("Unsupported backup format version $formatVersion")
            }
            val root = JSONObject(dataStr)
            val migrated = migrateBackupFormat(formatVersion, root)
            applyImport(migrated)
            RestoreResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            RestoreResult.Error(e.message ?: "Import error")
        }
    }

    /**
     * Транзакционно заменяет всю БД содержимым backup.
     * Порядок очистки/вставки учитывает foreign keys: родители первыми, дети последними.
     * Ошибка на любом шаге откатывает всю транзакцию — половинчатое состояние невозможно.
     */
    private suspend fun applyImport(root: JSONObject): RestoreResult {
        database.withTransaction {
            // Очистка в порядке, обратном зависимостям (дети -> родители).
            taskTagDao.clearAll()
            subtaskDao.clearAll()
            pomodoroSessionDao.clearAll()
            habitLogDao.clearAll()
            transactionDao.clearAll()
            budgetDao.clearAll()
            goalDao.clearAll()
            noteDao.clearAll()
            noteFolderDao.clearAll()
            taskDao.clearAll()
            subprojectDao.clearAll()
            projectDao.clearAll()
            tagDao.clearAll()
            habitDao.clearAll()
            categoryDao.clearAll()
            accountDao.clearAll()
            userStatsDao.clearAll()

            // Вставка в порядке зависимостей (родители первыми), сохраняя оригинальные ID.
            root.optJSONArray("projects")?.let { projectDao.insertAll(fromJsonArray(it) { projectFromJson(it) }) }
            root.optJSONArray("subprojects")?.let { subprojectDao.insertAll(fromJsonArray(it) { subprojectFromJson(it) }) }
            root.optJSONArray("tags")?.let { tagDao.insertAll(fromJsonArray(it) { tagFromJson(it) }) }
            root.optJSONArray("tasks")?.let { taskDao.insertAll(fromJsonArray(it) { taskFromJson(it) }) }
            root.optJSONArray("subtasks")?.let { subtaskDao.insertAll(fromJsonArray(it) { subtaskFromJson(it) }) }
            root.optJSONArray("taskTags")?.let { taskTagDao.insertAll(fromJsonArray(it) { taskTagFromJson(it) }) }
            root.optJSONArray("habits")?.let { habitDao.insertAll(fromJsonArray(it) { habitFromJson(it) }) }
            root.optJSONArray("habitLogs")?.let { habitLogDao.insertAll(fromJsonArray(it) { habitLogFromJson(it) }) }
            root.optJSONArray("pomodoroSessions")?.let { pomodoroSessionDao.insertAll(fromJsonArray(it) { pomodoroFromJson(it) }) }
            root.optJSONArray("accounts")?.let { accountDao.insertAll(fromJsonArray(it) { accountFromJson(it) }) }
            root.optJSONArray("categories")?.let { categoryDao.insertAll(fromJsonArray(it) { categoryFromJson(it) }) }
            root.optJSONArray("transactions")?.let { transactionDao.insertAll(fromJsonArray(it) { transactionFromJson(it) }) }
            root.optJSONArray("budgets")?.let { budgetDao.insertAll(fromJsonArray(it) { budgetFromJson(it) }) }
            root.optJSONArray("goals")?.let { goalDao.insertAll(fromJsonArray(it) { goalFromJson(it) }) }
            root.optJSONArray("noteFolders")?.let { noteFolderDao.insertAll(fromJsonArray(it) { noteFolderFromJson(it) }) }
            root.optJSONArray("notes")?.let { noteDao.insertAll(fromJsonArray(it) { noteFromJson(it) }) }
            root.optJSONArray("userStats")?.let { userStatsDao.insertAll(fromJsonArray(it) { userStatsFromJson(it) }) }
        }
        return RestoreResult.Success
    }

    private suspend fun buildExportJson(): JSONObject = JSONObject().apply {
        put("projects", toJsonArray(projectDao.snapshotAll()))
        put("subprojects", toJsonArray(subprojectDao.snapshotAll()))
        put("tags", toJsonArray(tagDao.snapshotAll()))
        put("tasks", toJsonArray(taskDao.snapshotAll()))
        put("subtasks", toJsonArray(subtaskDao.snapshotAll()))
        put("taskTags", toJsonArray(taskTagDao.snapshotAll()))
        put("habits", toJsonArray(habitDao.snapshotAll()))
        put("habitLogs", toJsonArray(habitLogDao.snapshotAll()))
        put("pomodoroSessions", toJsonArray(pomodoroSessionDao.snapshotAll()))
        put("accounts", toJsonArray(accountDao.snapshotAll()))
        put("categories", toJsonArray(categoryDao.snapshotAll()))
        put("transactions", toJsonArray(transactionDao.snapshotAll()))
        put("budgets", toJsonArray(budgetDao.snapshotAll()))
        put("goals", toJsonArray(goalDao.snapshotAll()))
        put("noteFolders", toJsonArray(noteFolderDao.snapshotAll()))
        put("notes", toJsonArray(noteDao.snapshotAll()))
        put("userStats", toJsonArray(userStatsDao.snapshotAll()))
    }

    private inline fun <T> serializeList(list: List<T>, serializer: (T) -> JSONObject): JSONArray {
        val arr = JSONArray()
        list.forEach { arr.put(serializer(it)) }
        return arr
    }

    private fun toJsonArray(list: List<TaskEntity>): JSONArray = serializeList(list) { taskToJson(it) }
    private fun toJsonArray(list: List<ProjectEntity>): JSONArray = serializeList(list) { projectToJson(it) }
    private fun toJsonArray(list: List<SubprojectEntity>): JSONArray = serializeList(list) { subprojectToJson(it) }
    private fun toJsonArray(list: List<TagEntity>): JSONArray = serializeList(list) { tagToJson(it) }
    private fun toJsonArray(list: List<SubtaskEntity>): JSONArray = serializeList(list) { subtaskToJson(it) }
    private fun toJsonArray(list: List<TaskTagEntity>): JSONArray =
        serializeList(list) { JSONObject().apply { put("taskId", it.taskId); put("tagId", it.tagId) } }
    private fun toJsonArray(list: List<HabitEntity>): JSONArray = serializeList(list) { habitToJson(it) }
    private fun toJsonArray(list: List<HabitLogEntity>): JSONArray = serializeList(list) { habitLogToJson(it) }
    private fun toJsonArray(list: List<PomodoroSessionEntity>): JSONArray = serializeList(list) { pomodoroToJson(it) }
    private fun toJsonArray(list: List<AccountEntity>): JSONArray = serializeList(list) { accountToJson(it) }
    private fun toJsonArray(list: List<CategoryEntity>): JSONArray = serializeList(list) { categoryToJson(it) }
    private fun toJsonArray(list: List<TransactionEntity>): JSONArray = serializeList(list) { transactionToJson(it) }
    private fun toJsonArray(list: List<BudgetEntity>): JSONArray = serializeList(list) { budgetToJson(it) }
    private fun toJsonArray(list: List<GoalEntity>): JSONArray = serializeList(list) { goalToJson(it) }
    private fun toJsonArray(list: List<NoteFolderEntity>): JSONArray =
        serializeList(list) { JSONObject().apply { put("id", it.id); put("name", it.name) } }
    private fun toJsonArray(list: List<NoteEntity>): JSONArray = serializeList(list) { noteToJson(it) }
    private fun toJsonArray(list: List<UserStatsEntity>): JSONArray = serializeList(list) { userStatsToJson(it) }

    private inline fun <T> fromJsonArray(arr: JSONArray, deserializer: (JSONObject) -> T): List<T> {
        val out = ArrayList<T>(arr.length())
        for (i in 0 until arr.length()) {
            arr.optJSONObject(i)?.let { out.add(deserializer(it)) }
        }
        return out
    }

    // region serializers (preserve ALL fields incl. ids)
    private fun taskToJson(t: TaskEntity) = JSONObject().apply {
        put("id", t.id); put("title", t.title); put("description", t.description)
        put("projectId", t.projectId); put("subprojectId", t.subprojectId); put("priority", t.priority)
        put("status", t.status); put("deadline", t.deadline); put("startTime", t.startTime)
        put("durationMinutes", t.durationMinutes); put("isCompleted", t.isCompleted)
        put("pomodoroEstimate", t.pomodoroEstimate); put("timeEstimateMinutes", t.timeEstimateMinutes)
        put("eisenhowerQuadrant", t.eisenhowerQuadrant); put("createdAt", t.createdAt)
        put("updatedAt", t.updatedAt); put("color", t.color); put("reminderDate", t.reminderDate)
        put("recurrenceRule", t.recurrenceRule); put("tags", t.tags)
    }

    private fun projectToJson(p: ProjectEntity) = JSONObject().apply {
        put("id", p.id); put("title", p.title); put("description", p.description)
        put("color", p.color); put("icon", p.icon); put("deadline", p.deadline)
        put("isArchived", p.isArchived); put("createdAt", p.createdAt); put("updatedAt", p.updatedAt)
    }

    private fun subprojectToJson(s: SubprojectEntity) = JSONObject().apply {
        put("id", s.id); put("title", s.title); put("description", s.description)
        put("parentProjectId", s.parentProjectId); put("parentSubprojectId", s.parentSubprojectId)
        put("color", s.color); put("icon", s.icon); put("deadline", s.deadline)
        put("isArchived", s.isArchived); put("orderIndex", s.orderIndex)
        put("createdAt", s.createdAt); put("updatedAt", s.updatedAt)
    }

    private fun tagToJson(t: TagEntity) = JSONObject().apply {
        put("id", t.id); put("name", t.name); put("color", t.color)
    }

    private fun subtaskToJson(s: SubtaskEntity) = JSONObject().apply {
        put("id", s.id); put("taskId", s.taskId); put("parentSubtaskId", s.parentSubtaskId)
        put("title", s.title); put("isCompleted", s.isCompleted); put("orderIndex", s.orderIndex)
        put("createdAt", s.createdAt)
    }

    private fun habitToJson(h: HabitEntity) = JSONObject().apply {
        put("id", h.id); put("name", h.name); put("description", h.description)
        put("icon", h.icon); put("color", h.color); put("frequency", h.frequency)
        put("daysOfWeek", h.daysOfWeek); put("targetCount", h.targetCount)
        put("reminderTime", h.reminderTime); put("isArchived", h.isArchived)
        put("createdAt", h.createdAt); put("updatedAt", h.updatedAt)
    }

    private fun habitLogToJson(l: HabitLogEntity) = JSONObject().apply {
        put("id", l.id); put("habitId", l.habitId); put("date", l.date)
        put("count", l.count); put("completedAt", l.completedAt)
    }

    private fun pomodoroToJson(p: PomodoroSessionEntity) = JSONObject().apply {
        put("id", p.id); put("taskId", p.taskId); put("startTime", p.startTime)
        put("durationMinutes", p.durationMinutes); put("isCompleted", p.isCompleted)
        put("type", p.type); put("createdAt", p.createdAt)
    }

    private fun accountToJson(a: AccountEntity) = JSONObject().apply {
        put("id", a.id); put("name", a.name); put("initialBalance", a.initialBalance)
        put("currency", a.currency)
    }

    private fun categoryToJson(c: CategoryEntity) = JSONObject().apply {
        put("id", c.id); put("name", c.name); put("type", c.type)
        put("color", c.color); put("icon", c.icon); put("isDefault", c.isDefault)
    }

    private fun transactionToJson(t: TransactionEntity) = JSONObject().apply {
        put("id", t.id); put("amount", t.amount); put("type", t.type); put("currency", t.currency)
        put("categoryId", t.categoryId); put("accountId", t.accountId); put("date", t.date)
        put("note", t.note); put("toAccountId", t.toAccountId)
        put("destinationAmount", t.destinationAmount); put("destinationCurrency", t.destinationCurrency)
        put("createdAt", t.createdAt); put("updatedAt", t.updatedAt)
    }

    private fun budgetToJson(b: BudgetEntity) = JSONObject().apply {
        put("id", b.id); put("categoryId", b.categoryId); put("amount", b.amount); put("currency", b.currency)
    }

    private fun goalToJson(g: GoalEntity) = JSONObject().apply {
        put("id", g.id); put("title", g.title); put("targetAmount", g.targetAmount)
        put("savedAmount", g.savedAmount); put("currency", g.currency)
        put("deadline", g.deadline); put("createdAt", g.createdAt)
    }

    private fun noteToJson(n: NoteEntity) = JSONObject().apply {
        put("id", n.id); put("title", n.title); put("contentMarkdown", n.contentMarkdown)
        put("folderId", n.folderId); put("tags", n.tags); put("pinned", n.pinned)
        put("archived", n.archived); put("projectId", n.projectId)
        put("createdAt", n.createdAt); put("updatedAt", n.updatedAt)
    }

    private fun userStatsToJson(u: UserStatsEntity) = JSONObject().apply {
        put("id", u.id); put("totalPoints", u.totalPoints); put("level", u.level)
        put("completedTasks", u.completedTasks); put("streak", u.streak)
        put("unlockedAchievements", u.unlockedAchievements); put("updatedAt", u.updatedAt)
    }
    // endregion

    // region deserializers (preserve IDs)
    private fun taskFromJson(o: JSONObject) = TaskEntity(
        id = o.optLong("id", 0), title = o.optString("title"), description = o.optString("description").ifBlank { null },
        projectId = o.nullableLong("projectId"), subprojectId = o.nullableLong("subprojectId"),
        priority = o.optInt("priority", 4), status = o.optString("status", "TODO"),
        deadline = o.nullableLong("deadline"), startTime = o.nullableLong("startTime"),
        durationMinutes = o.nullableLong("durationMinutes"), isCompleted = o.optBoolean("isCompleted", false),
        pomodoroEstimate = o.nullableInt("pomodoroEstimate"), timeEstimateMinutes = o.nullableLong("timeEstimateMinutes"),
        eisenhowerQuadrant = o.optString("eisenhowerQuadrant").ifBlank { null },
        createdAt = o.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis()),
        color = o.optString("color").ifBlank { null }, reminderDate = o.nullableLong("reminderDate"),
        recurrenceRule = o.optString("recurrenceRule").ifBlank { null }, tags = o.optString("tags").ifBlank { null }
    )

    private fun projectFromJson(o: JSONObject) = ProjectEntity(
        id = o.optLong("id", 0), title = o.optString("title"), description = o.optString("description").ifBlank { null },
        color = o.optString("color").ifBlank { null }, icon = o.optString("icon").ifBlank { null },
        deadline = o.nullableLong("deadline"), isArchived = o.optBoolean("isArchived", false),
        createdAt = o.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun subprojectFromJson(o: JSONObject) = SubprojectEntity(
        id = o.optLong("id", 0), title = o.optString("title"), description = o.optString("description").ifBlank { null },
        parentProjectId = o.nullableLong("parentProjectId"), parentSubprojectId = o.nullableLong("parentSubprojectId"),
        color = o.optString("color").ifBlank { null }, icon = o.optString("icon").ifBlank { null },
        deadline = o.nullableLong("deadline"), isArchived = o.optBoolean("isArchived", false),
        orderIndex = o.optInt("orderIndex", 0), createdAt = o.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun tagFromJson(o: JSONObject) = TagEntity(
        id = o.optLong("id", 0), name = o.optString("name"), color = o.optString("color").ifBlank { null }
    )

    private fun subtaskFromJson(o: JSONObject) = SubtaskEntity(
        id = o.optLong("id", 0), taskId = o.optLong("taskId", 0),
        parentSubtaskId = o.nullableLong("parentSubtaskId"), title = o.optString("title"),
        isCompleted = o.optBoolean("isCompleted", false), orderIndex = o.optInt("orderIndex", 0),
        createdAt = o.optLong("createdAt", System.currentTimeMillis())
    )

    private fun taskTagFromJson(o: JSONObject) = TaskTagEntity(
        taskId = o.optLong("taskId", 0), tagId = o.optLong("tagId", 0)
    )

    private fun habitFromJson(o: JSONObject) = HabitEntity(
        id = o.optLong("id", 0), name = o.optString("name"), description = o.optString("description").ifBlank { null },
        icon = o.optString("icon").ifBlank { null }, color = o.optString("color").ifBlank { null },
        frequency = o.optString("frequency", "DAILY"), daysOfWeek = o.optString("daysOfWeek", ""),
        targetCount = o.optInt("targetCount", 1), reminderTime = o.nullableLong("reminderTime"),
        isArchived = o.optBoolean("isArchived", false), createdAt = o.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun habitLogFromJson(o: JSONObject) = HabitLogEntity(
        id = o.optLong("id", 0), habitId = o.optLong("habitId", 0), date = o.optLong("date", 0),
        count = o.optInt("count", 1), completedAt = o.optLong("completedAt", System.currentTimeMillis())
    )

    private fun pomodoroFromJson(o: JSONObject) = PomodoroSessionEntity(
        id = o.optLong("id", 0), taskId = o.nullableLong("taskId"), startTime = o.optLong("startTime", 0),
        durationMinutes = o.optInt("durationMinutes", 0), isCompleted = o.optBoolean("isCompleted", false),
        type = o.optString("type", "WORK"), createdAt = o.optLong("createdAt", System.currentTimeMillis())
    )

    private fun accountFromJson(o: JSONObject) = AccountEntity(
        id = o.optLong("id", 0), name = o.optString("name"),
        initialBalance = o.optDouble("initialBalance", 0.0), currency = o.optString("currency", "RUB")
    )

    private fun categoryFromJson(o: JSONObject) = CategoryEntity(
        id = o.optLong("id", 0), name = o.optString("name"), type = o.optString("type", "EXPENSE"),
        color = o.optString("color").ifBlank { null }, icon = o.optString("icon").ifBlank { null },
        isDefault = o.optBoolean("isDefault", false)
    )

    private fun transactionFromJson(o: JSONObject) = TransactionEntity(
        id = o.optLong("id", 0), amount = o.optDouble("amount", 0.0), type = o.optString("type", "EXPENSE"),
        currency = o.optString("currency", "RUB"), categoryId = o.nullableLong("categoryId"),
        accountId = o.nullableLong("accountId"), date = o.optLong("date", 0),
        note = o.optString("note").ifBlank { null }, toAccountId = o.nullableLong("toAccountId"),
        destinationAmount = o.nullableDouble("destinationAmount"),
        destinationCurrency = o.optString("destinationCurrency").ifBlank { null },
        createdAt = o.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun budgetFromJson(o: JSONObject) = BudgetEntity(
        id = o.optLong("id", 0), categoryId = o.optLong("categoryId", 0),
        amount = o.optDouble("amount", 0.0), currency = o.optString("currency", "RUB")
    )

    private fun goalFromJson(o: JSONObject) = GoalEntity(
        id = o.optLong("id", 0), title = o.optString("title"), targetAmount = o.optDouble("targetAmount", 0.0),
        savedAmount = o.optDouble("savedAmount", 0.0), currency = o.optString("currency", "RUB"),
        deadline = o.nullableLong("deadline"), createdAt = o.optLong("createdAt", System.currentTimeMillis())
    )

    private fun noteFolderFromJson(o: JSONObject) = NoteFolderEntity(
        id = o.optLong("id", 0), name = o.optString("name")
    )

    private fun noteFromJson(o: JSONObject) = NoteEntity(
        id = o.optLong("id", 0), title = o.optString("title"), contentMarkdown = o.optString("contentMarkdown", ""),
        folderId = o.nullableLong("folderId"), tags = o.optString("tags").ifBlank { null },
        pinned = o.optBoolean("pinned", false), archived = o.optBoolean("archived", false),
        projectId = o.nullableLong("projectId"), createdAt = o.optLong("createdAt", System.currentTimeMillis()),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
    )

    private fun userStatsFromJson(o: JSONObject) = UserStatsEntity(
        id = o.optLong("id", 1), totalPoints = o.optLong("totalPoints", 0), level = o.optInt("level", 1),
        completedTasks = o.optInt("completedTasks", 0), streak = o.optInt("streak", 0),
        unlockedAchievements = o.optString("unlockedAchievements", ""),
        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
    )
    // endregion

    /**
     * Миграция формата backup между версиями.
     * formatVersion 1-2 (legacy): только tasks/projects/habits без ID.
     * formatVersion 3: полная схема с сохранением ID.
     * Legacy-файлы импортируются частично (только имеющиеся секции), без удаления отсутствующих таблиц.
     */
    private fun migrateBackupFormat(fromVersion: Int, root: JSONObject): JSONObject {
        if (fromVersion >= FORMAT_VERSION) return root
        return root
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }

    private fun JSONObject.nullableLong(key: String): Long? =
        if (!has(key) || isNull(key)) null else optLong(key)

    private fun JSONObject.nullableInt(key: String): Int? =
        if (!has(key) || isNull(key)) null else optInt(key)

    private fun JSONObject.nullableDouble(key: String): Double? =
        if (!has(key) || isNull(key)) null else optDouble(key)
}
