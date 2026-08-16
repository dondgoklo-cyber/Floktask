package com.taskmanager.domain.usecase.backup

import com.taskmanager.domain.model.BackupFile
import com.taskmanager.domain.repository.BackupRepository
import java.io.OutputStream
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(output: OutputStream): BackupFile {
        val file = backupRepository.export()
        backupRepository.serialize(file, output)
        return file
    }
}
