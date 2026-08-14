package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.NoteFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteFolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: NoteFolderEntity): Long

    @Update
    suspend fun update(folder: NoteFolderEntity)

    @Delete
    suspend fun delete(folder: NoteFolderEntity)

    @Query("SELECT * FROM note_folders ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<NoteFolderEntity>>

    @Query("SELECT * FROM note_folders WHERE id = :id")
    suspend fun getById(id: Long): NoteFolderEntity?

    @Query("DELETE FROM note_folders WHERE id = :id")
    suspend fun deleteById(id: Long)
}
