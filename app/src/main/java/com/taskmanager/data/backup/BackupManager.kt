package com.taskmanager.data.backup

import android.content.Context
import android.net.Uri
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.data.local.dao.HabitDao
import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.data.local.entity.ProjectEntity
import com.taskmanager.data.local.entity.HabitEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Экспорт/импорт данных в JSON. Полностью оффлайн: чтение/запись через ContentResolver.
 * Формат: { "tasks": [...], "projects": [...], "habits": [...] }
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val habitDao: HabitDao
) {
    suspend fun exportToUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = buildExportJson()
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(json.toString(2).toByteArray(Charsets.UTF_8))
            } ?: return@withContext false
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun importFromUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonStr = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader(Charsets.UTF_8).readText()
            } ?: return@withContext false
            val root = JSONObject(jsonStr)
            importProjects(root.optJSONArray("projects"))
            importTasks(root.optJSONArray("tasks"))
            importHabits(root.optJSONArray("habits"))
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun buildExportJson(): JSONObject {
        val json = JSONObject()
        val tasks = taskDao.getAll().first()
        val projects = projectDao.getAll().first()
        val habits = habitDao.getAll().first()

        val tasksArr = JSONArray()
        tasks.forEach { tasksArr.put(taskToJson(it)) }
        json.put("tasks", tasksArr)

        val projectsArr = JSONArray()
        projects.forEach { projectsArr.put(projectToJson(it)) }
        json.put("projects", projectsArr)

        val habitsArr = JSONArray()
        habits.forEach { habitsArr.put(habitToJson(it)) }
        json.put("habits", habitsArr)

        return json
    }

    private fun taskToJson(t: TaskEntity): JSONObject = JSONObject().apply {
        put("title", t.title)
        put("description", t.description)
        put("projectId", t.projectId)
        put("priority", t.priority)
        put("status", t.status)
        put("deadline", t.deadline)
        put("startTime", t.startTime)
        put("durationMinutes", t.durationMinutes)
        put("isCompleted", t.isCompleted)
        put("pomodoroEstimate", t.pomodoroEstimate)
        put("eisenhowerQuadrant", t.eisenhowerQuadrant)
        put("reminderDate", t.reminderDate)
        put("recurrenceRule", t.recurrenceRule)
        put("tags", t.tags)
    }

    private fun projectToJson(p: ProjectEntity): JSONObject = JSONObject().apply {
        put("title", p.title)
        put("description", p.description)
        put("color", p.color)
        put("deadline", p.deadline)
        put("isArchived", p.isArchived)
    }

    private fun habitToJson(h: HabitEntity): JSONObject = JSONObject().apply {
        put("name", h.name)
        put("description", h.description)
        put("frequency", h.frequency)
        put("targetCount", h.targetCount)
        put("reminderTime", h.reminderTime)
        put("isArchived", h.isArchived)
    }

    private suspend fun importTasks(arr: JSONArray?) {
        if (arr == null) return
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            taskDao.insert(TaskEntity(
                title = o.getString("title"),
                description = o.optString("description").ifBlank { null },
                projectId = if (o.isNull("projectId")) null else o.optLong("projectId"),
                priority = o.optInt("priority", 4),
                status = o.optString("status", "TODO"),
                deadline = if (o.isNull("deadline")) null else o.optLong("deadline"),
                startTime = if (o.isNull("startTime")) null else o.optLong("startTime"),
                durationMinutes = if (o.isNull("durationMinutes")) null else o.optLong("durationMinutes"),
                isCompleted = o.optBoolean("isCompleted", false),
                pomodoroEstimate = if (o.isNull("pomodoroEstimate")) null else o.optInt("pomodoroEstimate"),
                eisenhowerQuadrant = o.optString("eisenhowerQuadrant").ifBlank { null },
                reminderDate = if (o.isNull("reminderDate")) null else o.optLong("reminderDate"),
                recurrenceRule = o.optString("recurrenceRule").ifBlank { null },
                tags = o.optString("tags").ifBlank { null }
            ))
        }
    }

    private suspend fun importProjects(arr: JSONArray?) {
        if (arr == null) return
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            projectDao.insert(ProjectEntity(
                title = o.getString("title"),
                description = o.optString("description").ifBlank { null },
                color = o.optString("color", "#FF6D00"),
                deadline = if (o.isNull("deadline")) null else o.optLong("deadline"),
                isArchived = o.optBoolean("isArchived", false)
            ))
        }
    }

    private suspend fun importHabits(arr: JSONArray?) {
        if (arr == null) return
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            habitDao.insert(HabitEntity(
                name = o.getString("name"),
                description = o.optString("description").ifBlank { null },
                frequency = o.optString("frequency", "DAILY"),
                targetCount = o.optInt("targetCount", 1),
                reminderTime = if (o.isNull("reminderTime")) null else o.optLong("reminderTime"),
                isArchived = o.optBoolean("isArchived", false)
            ))
        }
    }
