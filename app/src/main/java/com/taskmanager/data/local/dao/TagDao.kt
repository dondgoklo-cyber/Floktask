package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(tag: TagEntity): Long

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("SELECT * FROM tags WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun findByName(name: String): TagEntity?

    @Query("SELECT * FROM tags")
    fun getAll(): Flow<List<TagEntity>>

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteById(id: Long)
}
