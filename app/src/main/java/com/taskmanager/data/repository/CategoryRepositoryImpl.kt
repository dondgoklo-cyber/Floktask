package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.CategoryDao
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun createCategory(category: Category): Long = try {
        categoryDao.insert(category.toEntity())
    } catch (e: Exception) {
        Log.e("CategoryRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateCategory(category: Category) {
        try {
            categoryDao.update(category.toEntity())
        } catch (e: Exception) {
            Log.e("CategoryRepositoryImpl", "Error in Category", e)
            throw e
        }
    }

    override suspend fun deleteCategory(id: Long) {
        try {
            categoryDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("CategoryRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllCategories(): Flow<List<Category>> = try {
        categoryDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("CategoryRepositoryImpl", "Error in Flow<List<Category>>", e)
        throw e
    }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> =
        categoryDao.getByType(type.name).map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryCount(): Int = try {
        categoryDao.count()
    } catch (e: Exception) {
        Log.e("CategoryRepositoryImpl", "Error in Int", e)
        throw e
    }
}
