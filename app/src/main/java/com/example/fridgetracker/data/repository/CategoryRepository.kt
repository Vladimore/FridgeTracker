package com.example.fridgetracker.data.repository

import com.example.fridgetracker.data.local.CategoryDao
import com.example.fridgetracker.data.local.CategoryEntity
import com.example.fridgetracker.data.local.CategoryFieldDefinitionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CategoryRepository(private val dao: CategoryDao) {
    fun observeCategories(familyId: String): Flow<List<CategoryEntity>> = dao.observeAll(familyId)

    fun observeFields(categoryId: String): Flow<List<CategoryFieldDefinitionEntity>> = dao.observeFields(categoryId)

    suspend fun addCategory(name: String, color: String = "#E5E7EB"): CategoryEntity {
        val now = System.currentTimeMillis()
        return CategoryEntity(UUID.randomUUID().toString(), "local-family", name.trim(), color, now, now).also {
            dao.insert(it)
        }
    }

    suspend fun addField(categoryId: String, name: String, type: String, sortOrder: Int) {
        dao.insertField(
            CategoryFieldDefinitionEntity(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                fieldType = type,
                fieldName = name.trim(),
                sortOrder = sortOrder
            )
        )
    }

    suspend fun deleteCategory(category: CategoryEntity) = dao.softDelete(category.id)
    suspend fun deleteField(field: CategoryFieldDefinitionEntity) = dao.deleteField(field.id)
}