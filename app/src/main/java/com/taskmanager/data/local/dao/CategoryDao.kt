package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("SELECT * FROM finance_categories ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM finance_categories WHERE type = :type ORDER BY name COLLATE NOCASE ASC")
    fun getByType(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM finance_categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("DELETE FROM finance_categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM finance_categories")
    suspend fun count(): Int
}
