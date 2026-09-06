package com.taskmanager.data.backup

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * \u042d\u043a\u0441\u043f\u043e\u0440\u0442/\u0438\u043c\u043f\u043e\u0440\u0442 \u0434\u0430\u043d\u043d\u044b\u0445 \u0432 JSON. \u0424\u043e\u0440\u043c\u0430\u0442: { "tasks": [...], "projects": [...], "habits": [...] }
 * \u0424\u043e\u0440\u043c\u0430\u0442: { "tasks": [...], "projects": [...], "habits": [...] }
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
        private const val PREFS_NAME = "floktask_backup_prefs"
        private const val KEY_ENCRYPTION_KEY = "encryption_key"
        private const val MASTER_KEY_ALIAS = "floktask_master_key"
    }

    private val securePrefs by lazy {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM)
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val encryptionKey: String
        get() = securePrefs.getString(KEY_ENCRYPTION_KEY, null) ?: run {
            val newKey = generateSecureKey()
            securePrefs.edit().putString(KEY_ENCRYPTION_KEY, newKey).apply()
            newKey
        }

    private fun generateSecureKey(): String {
        return (1..32).map { ('a'..'z' + 'A'..'Z' + '0'..'9').random() }.joinToString("")
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
                    put("dueDate", task.dueDate)
                    put("createdAt", task.createdAt)
                    put("updatedAt", task.updatedAt)
                    put("projectId", task.projectId)
                    put("eisenhowerQuadrant", task.eisenhowerQuadrant)
                    put("isCompleted", task.isCompleted)
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
                    put("name", project.name)
                    put("description", project.description)
                    put("color", project.color)
                    put("createdAt", project.createdAt)
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
                    put("title", habit.title)
                    put("description", habit.description)
                    put("frequency", habit.frequency)
                    put("createdAt", habit.createdAt)
                    put("lastCompletion", habit.lastCompletion)
                    put("completionCount", habit.completionCount)
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
                status = taskObj.optString("status", "TODO"),
                priority = taskObj.optInt("priority", 3),
                dueDate = taskObj.optLong("dueDate", 0),
                createdAt = taskObj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = taskObj.optLong("updatedAt", System.currentTimeMillis()),
                projectId = taskObj.optLong("projectId", null),
                eisenhowerQuadrant = taskObj.optString("eisenhowerQuadrant", null),
                isCompleted = taskObj.optBoolean("isCompleted", false),
                tags = taskObj.optString("tags", null)
            )
            taskDao.upsert(entity)
        }
    }

    private suspend fun importProjects(data: JSONObject) = withContext(Dispatchers.IO) {
        val projectsArray = data.optJSONArray("projects") ?: return@withContext
        for (i in 0 until projectsArray.length()) {
            val projectObj = projectsArray.getJSONObject(i)
            val entity = ProjectEntity(
                id = projectObj.optLong("id", 0),
                name = projectObj.optString("name", ""),
                description = projectObj.optString("description", null),
                color = projectObj.optInt("color", 0),
                createdAt = projectObj.optLong("createdAt", System.currentTimeMillis())
            )
            projectDao.upsert(entity)
        }
    }

    private suspend fun importHabits(data: JSONObject) = withContext(Dispatchers.IO) {
        val habitsArray = data.optJSONArray("habits") ?: return@withContext
        for (i in 0 until habitsArray.length()) {
            val habitObj = habitsArray.getJSONObject(i)
            val entity = HabitEntity(
                id = habitObj.optLong("id", 0),
                title = habitObj.optString("title", ""),
                description = habitObj.optString("description", null),
                frequency = habitObj.optString("frequency", "DAILY"),
                createdAt = habitObj.optLong("createdAt", System.currentTimeMillis()),
                lastCompletion = habitObj.optLong("lastCompletion", 0),
                completionCount = habitObj.optInt("completionCount", 0)
            )
            habitDao.upsert(entity)
        }
    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }

    suspend fun encryptData(data: String): String = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keyBytes = encryptionKey.toByteArray(Charsets.UTF_8)
        val key = SecretKeySpec(keyBytes.copyOf(32), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val result = iv + encrypted
        Base64.encodeToString(result, Base64.NO_WRAP)
    }

    suspend fun decryptData(encrypted: String): String? = withContext(Dispatchers.IO) {
        try {
            val data = Base64.decode(encrypted, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keyBytes = encryptionKey.toByteArray(Charsets.UTF_8)
            val key = SecretKeySpec(keyBytes.copyOf(32), "AES")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, data.copyOfRange(0, 12)))
            val decrypted = cipher.doFinal(data.copyOfRange(12, data.size))
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
}
