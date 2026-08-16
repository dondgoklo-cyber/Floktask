package com.taskmanager.domain.usecase.backup

import com.taskmanager.domain.repository.BackupRepository
import com.taskmanager.domain.repository.RestoreResult
import java.io.InputStream
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(input: InputStream): RestoreResult {
        val file = backupRepository.deserialize(input)
        return backupRepository.restore(file)
    }
}
