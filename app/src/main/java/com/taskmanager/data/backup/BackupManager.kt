package com.taskmanager.data.backup

import android.content.Context
import android.net.Uri
import android.util.Base64
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
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backup and restore manager for tasks, projects, and habits.
 * Exports data to JSON format and imports from JSON.
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val habitDao: HabitDao
) {
    companion object {
        private const val BACKUP_VERSION = 2
    }

    suspend fun exportToUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = buildExportJson()
            val jsonStr = json.toString(2)
            val checksum = sha256(jsonStr)
            
            val backup = JSONObject()
            backup.put("version", BACKUP_VERSION)
            backup.put("checksum", checksum)
            backup.put("encrypted", false)
            backup.put("data", jsonStr)
            
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(backup.toString(2).toByteArray(Charsets.UTF_8))
            } ?: return@withContext false
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun importFromUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupStr = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader(Charsets.UTF_8).readText()
            } ?: return@withContext false
            
            val backup = JSONObject(backupStr)
            val dataStr = backup.optString("data", backupStr)
            val expectedChecksum = backup.optString("checksum", "")
            
            // Verify checksum
            if (expectedChecksum.isNotEmpty()) {
                val actualChecksum = sha256(dataStr)
                if (actualChecksum != expectedChecksum) {
                    return@withContext false
                }
            }
            
            val data = JSONObject(dataStr)
            
            // Import tasks
            importTasks(data)
            // Import projects
            importProjects(data)
            // Import habits
            importHabits(data)
            
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun buildExportJson(): JSONObject = withContext(Dispatchers.IO) {
        val json = JSONObject()
        
        // Export tasks
        val tasks = taskDao.getAll().first()
        val tasksArray = JSONArray().apply {
            tasks.forEach { task ->
                put(JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("status", task.status)
                    put("priority", task.priority)
                    put("deadline", task.deadline)
                    put("startTime", task.startTime)
                    put("durationMinutes", task.durationMinutes)
                    put("createdAt", task.createdAt)
                    put("updatedAt", task.updatedAt)
                    put("projectId", task.projectId)
                    put("eisenhowerQuadrant", task.eisenhowerQuadrant)
                    put("isCompleted", task.isCompleted)
                    put("pomodoroEstimate", task.pomodoroEstimate)
                    put("timeEstimateMinutes", task.timeEstimateMinutes)
                    put("color", task.color)
                    put("reminderDate", task.reminderDate)
                    put("recurrenceRule", task.recurrenceRule)
                    put("tags", task.tags)
                })
            }
        }
        json.put("tasks", tasksArray)
        
        // Export projects
        val projects = projectDao.getAll().first()
        val projectsArray = JSONArray().apply {
            projects.forEach { project ->
                put(JSONObject().apply {
                    put("id", project.id)
                    put("title", project.title)
                    put("description", project.description)
                    put("color", project.color)
                    put("icon", project.icon)
                    put("deadline", project.deadline)
                    put("isArchived", project.isArchived)
                    put("createdAt", project.createdAt)
                    put("updatedAt", project.updatedAt)
                })
            }
        }
        json.put("projects", projectsArray)
        
        // Export habits
        val habits = habitDao.getAll().first()
        val habitsArray = JSONArray().apply {
            habits.forEach { habit ->
                put(JSONObject().apply {
                    put("id", habit.id)
                    put("name", habit.name)
                    put("description", habit.description)
                    put("icon", habit.icon)
                    put("color", habit.color)
                    put("frequency", habit.frequency)
                    put("daysOfWeek", habit.daysOfWeek)
                    put("targetCount", habit.targetCount)
                    put("reminderTime", habit.reminderTime)
                    put("isArchived", habit.isArchived)
                    put("createdAt", habit.createdAt)
                    put("updatedAt", habit.updatedAt)
                })
            }
        }
        json.put("habits", habitsArray)
        
        json
    }

    private suspend fun importTasks(data: JSONObject) = withContext(Dispatchers.IO) {
        val tasksArray = data.optJSONArray("tasks") ?: return@withContext
        for (i in 0 until tasksArray.length()) {
            val taskObj = tasksArray.getJSONObject(i)
            val entity = TaskEntity(
                id = taskObj.optLong("id", 0),
                title = taskObj.optString("title", ""),
                description = taskObj.optString("description", null),
                projectId = taskObj.optLong("projectId", 0).takeIf { it != 0L },
                priority = taskObj.optInt("priority", 4),
                status = taskObj.optString("status", "TODO"),
                deadline = taskObj.optLong("deadline", 0).takeIf { it != 0L },
                startTime = taskObj.optLong("startTime", 0).takeIf { it != 0L },
                durationMinutes = taskObj.optLong("durationMinutes", 0).takeIf { it != 0L },
                createdAt = taskObj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = taskObj.optLong("updatedAt", System.currentTimeMillis()),
                isCompleted = taskObj.optBoolean("isCompleted", false),
                pomodoroEstimate = taskObj.optInt("pomodoroEstimate", 0).takeIf { it != 0 },
                timeEstimateMinutes = taskObj.optLong("timeEstimateMinutes", 0).takeIf { it != 0L },
                eisenhowerQuadrant = taskObj.optString("eisenhowerQuadrant", null).takeIf { it.isNotEmpty() },
                color = taskObj.optString("color", null).takeIf { it.isNotEmpty() },
                reminderDate = taskObj.optLong("reminderDate", 0).takeIf { it != 0L },
                recurrenceRule = taskObj.optString("recurrenceRule", null).takeIf { it.isNotEmpty() },
                tags = taskObj.optString("tags", null).takeIf { it.isNotEmpty() }
            )
            if (entity.id == 0L) {
                taskDao.insert(entity)
            } else {
                taskDao.update(entity)
            }
        }
    }

    private suspend fun importProjects(data: JSONObject) = withContext(Dispatchers.IO) {
        val projectsArray = data.optJSONArray("projects") ?: return@withContext
        for (i in 0 until projectsArray.length()) {
            val projectObj = projectsArray.getJSONObject(i)
            val entity = ProjectEntity(
                id = projectObj.optLong("id", 0),
                title = projectObj.optString("title", ""),
                description = projectObj.optString("description", null).takeIf { it.isNotEmpty() },
                color = projectObj.optString("color", null).takeIf { it.isNotEmpty() },
                icon = projectObj.optString("icon", null).takeIf { it.isNotEmpty() },
                deadline = projectObj.optLong("deadline", 0).takeIf { it != 0L },
                isArchived = projectObj.optBoolean("isArchived", false),
                createdAt = projectObj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = projectObj.optLong("updatedAt", System.currentTimeMillis())
            )
            if (entity.id == 0L) {
                projectDao.insert(entity)
            } else {
                projectDao.update(entity)
            }
        }
    }

    private suspend fun importHabits(data: JSONObject) = withContext(Dispatchers.IO) {
        val habitsArray = data.optJSONArray("habits") ?: return@withContext
        for (i in 0 until habitsArray.length()) {
            val habitObj = habitsArray.getJSONObject(i)
            val entity = HabitEntity(
                id = habitObj.optLong("id", 0),
                name = habitObj.optString("name", ""),
                description = habitObj.optString("description", null).takeIf { it.isNotEmpty() },
                icon = habitObj.optString("icon", null).takeIf { it.isNotEmpty() },
                color = habitObj.optString("color", null).takeIf { it.isNotEmpty() },
                frequency = habitObj.optString("frequency", "DAILY"),
                daysOfWeek = habitObj.optString("daysOfWeek", ""),
                targetCount = habitObj.optInt("targetCount", 1),
                reminderTime = habitObj.optLong("reminderTime", 0).takeIf { it != 0L },
                isArchived = habitObj.optBoolean("isArchived", false),
                createdAt = habitObj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = habitObj.optLong("updatedAt", System.currentTimeMillis())
            )
            if (entity.id == 0L) {
                habitDao.insert(entity)
            } else {
                habitDao.update(entity)
            }
        }
    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }
}
