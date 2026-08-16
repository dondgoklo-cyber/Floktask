package com.taskmanager.data.backup

import com.google.gson.GsonBuilder
import com.taskmanager.data.backup.dto.BackupFileDto
import com.taskmanager.data.backup.toDomain
import com.taskmanager.data.backup.toDto
import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.repository.toDomain
import com.taskmanager.data.repository.toEntity
import com.taskmanager.domain.model.BackupData
import com.taskmanager.domain.model.BackupFile
import com.taskmanager.domain.model.BackupMetadata
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.BackupRepository
import com.taskmanager.domain.repository.LocalBackup
import com.taskmanager.domain.repository.RestoreResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val tagDao: TagDao
) : BackupRepository {

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    private val backupDir: File by lazy {
        File(context.filesDir, BACKUP_DIR_NAME).apply { if (!exists()) mkdirs() }
    }

    override suspend fun export(): BackupFile {
        val tasks = taskDao.getAll().first().map { it.toDomain() }
        val projects = projectDao.getAll().first().map { it.toDomain() }
        val tags = tagDao.getAll().first().map { it.toDomain() }

        return BackupFile(
            metadata = BackupMetadata(
                schemaVersion = BackupMetadata.CURRENT_SCHEMA_VERSION,
                appVersion = "1.0.0"
            ),
            data = BackupData(tasks = tasks, projects = projects, tags = tags)
        )
    }

    override suspend fun serialize(file: BackupFile, output: OutputStream) {
        val dto = file.toDto()
        OutputStreamWriter(output, StandardCharsets.UTF_8).use { writer ->
            gson.toJson(dto, writer)
        }
    }

    override suspend fun deserialize(input: InputStream): BackupFile {
        val dto: BackupFileDto = InputStreamReader(input, StandardCharsets.UTF_8).use { reader ->
            gson.fromJson(reader, BackupFileDto::class.java)
        } ?: error("Backup file is empty or malformed")

        require(dto.metadata.schemaVersion <= BackupMetadata.CURRENT_SCHEMA_VERSION) {
            "Unsupported backup schema version ${dto.metadata.schemaVersion} " +
                "(max supported: ${BackupMetadata.CURRENT_SCHEMA_VERSION})"
        }
        require(dto.metadata.schemaVersion >= MIN_SUPPORTED_SCHEMA_VERSION) {
            "Backup schema version ${dto.metadata.schemaVersion} is too old to restore " +
                "(min supported: $MIN_SUPPORTED_SCHEMA_VERSION)"
        }

        return dto.toDomain()
    }

    override suspend fun restore(file: BackupFile): RestoreResult {
        val data = file.data

        // Order matters: projects first (FK parent), then tasks (FK child), then tags.
        projectDao.clearAll()
        taskDao.clearAll()
        tagDao.clearAll()

        // Preserve original ids by inserting with REPLACE.
        data.projects.forEach { projectDao.insert(projectWithId(it)) }
        data.tasks.forEach { taskDao.insert(taskWithId(it)) }
        data.tags.forEach { tagDao.insert(tagWithId(it)) }

        return RestoreResult(
            tasksRestored = data.tasks.size,
            projectsRestored = data.projects.size,
            tagsRestored = data.tags.size
        )
    }

    override suspend fun saveToLocal(): String {
        val file = export()
        val name = "floktask_${Instant.now().atZone(ZoneId.systemDefault())
            .format(TIMESTAMP_FORMATTER)}.json"
        val target = File(backupDir, name)
        target.outputStream().use { output -> serialize(file, output) }
        pruneOldBackups()
        return name
    }

    override suspend fun loadFromLocal(fileName: String): BackupFile {
        val target = File(backupDir, fileName)
        require(target.exists() && target.isFile) { "Backup not found: $fileName" }
        require(target.canonicalPath.startsWith(backupDir.canonicalPath)) {
            "Attempt to read outside backup directory"
        }
        return target.inputStream().use { input -> deserialize(input) }
    }

    override suspend fun listLocalBackups(): List<LocalBackup> {
        return backupDir.listFiles { f -> f.isFile && f.name.endsWith(".json") }
            ?.sortedByDescending { it.lastModified() }
            ?.map { LocalBackup(it.name, it.lastModified(), it.length()) }
            ?: emptyList()
    }

    override suspend fun deleteLocalBackup(fileName: String): Boolean {
        val target = File(backupDir, fileName)
        if (!target.exists()) return false
        require(target.canonicalPath.startsWith(backupDir.canonicalPath)) {
            "Attempt to delete outside backup directory"
        }
        return target.delete()
    }

    /**
     * Keeps at most [MAX_LOCAL_BACKUPS] files, deleting the oldest.
     */
    private fun pruneOldBackups() {
        val files = backupDir.listFiles { f -> f.isFile && f.name.endsWith(".json") }
            ?.sortedByDescending { it.lastModified() } ?: return
        if (files.size > MAX_LOCAL_BACKUPS) {
            files.drop(MAX_LOCAL_BACKUPS).forEach { it.delete() }
        }
    }

    /**
     * Inserts preserve the original id when present so that FK relations
     * (task.projectId) survive restore. autoGenerate only kicks in for id == 0.
     */
    private fun projectWithId(project: Project) =
        project.toEntity().copy(id = project.id ?: 0)

    private fun taskWithId(task: Task) =
        task.toEntity().copy(id = task.id ?: 0)

    private fun tagWithId(tag: Tag) =
        tag.toEntity().copy(id = tag.id ?: 0)

    companion object {
        private const val MIN_SUPPORTED_SCHEMA_VERSION = 1
        private const val BACKUP_DIR_NAME = "backup"
        private const val MAX_LOCAL_BACKUPS = 10
        private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    }
}
