package com.example.fridgetracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class UserEntity(@PrimaryKey val id: String, val login: String, val email: String? = null, val emailVerified: Boolean = false, val createdAt: Long, val updatedAt: Long)

@Entity(tableName = "families")
data class FamilyEntity(@PrimaryKey val id: String, val ownerUserId: String, val createdAt: Long, val updatedAt: Long)

@Entity(tableName = "family_members", indices = [Index(value = ["familyId"]), Index(value = ["userId"])])
data class FamilyMemberEntity(@PrimaryKey val id: String, val familyId: String, val userId: String, val role: String, val status: String, val joinedAt: Long? = null)

@Entity(tableName = "categories", indices = [Index(value = ["familyId"])])
data class CategoryEntity(@PrimaryKey val id: String, val familyId: String, val name: String, val color: String = "#E5E7EB", val createdAt: Long, val updatedAt: Long, val isDeleted: Boolean = false)

@Entity(tableName = "category_field_definitions", indices = [Index(value = ["categoryId"])])
data class CategoryFieldDefinitionEntity(@PrimaryKey val id: String, val categoryId: String, val fieldType: String, val fieldName: String, val required: Boolean = false, val sortOrder: Int = 0)

@Entity(tableName = "product_field_values", indices = [Index(value = ["productId"]), Index(value = ["fieldDefinitionId"])])
data class ProductFieldValueEntity(@PrimaryKey val id: String, val productId: String, val fieldDefinitionId: String, val value: String)

@Entity(tableName = "barcode_product_cache")
data class BarcodeProductCacheEntity(@PrimaryKey val barcode: String, val name: String, val brand: String? = null, val imageUrl: String? = null, val additionalData: String? = null, val updatedAt: Long)

@Entity(tableName = "shopping_list_items", indices = [Index(value = ["familyId"])])
data class ShoppingListItemEntity(@PrimaryKey val id: String, val familyId: String, val productId: String? = null, val nameSnapshot: String, val quantity: Double, val createdAt: Long, val completed: Boolean = false)

@Entity(tableName = "product_actions", indices = [Index(value = ["familyId"]), Index(value = ["productId"])])
data class ProductActionEntity(@PrimaryKey val id: String, val familyId: String, val userId: String, val productId: String, val actionType: String, val quantityBefore: Double? = null, val quantityAfter: Double? = null, val timestamp: Long, val metadata: String? = null)

@Entity(tableName = "sync_operations", indices = [Index(value = ["status"]), Index(value = ["createdAt"])])
data class SyncOperationEntity(@PrimaryKey val id: String, val entityId: String, val entityType: String, val operationType: String, val payload: String, val createdAt: Long, val retryCount: Int = 0, val status: String = "PENDING")

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE familyId = :familyId AND isDeleted = 0 ORDER BY name")
    fun observeAll(familyId: String): Flow<List<CategoryEntity>>
    @Query("SELECT * FROM category_field_definitions WHERE categoryId = :categoryId ORDER BY sortOrder, fieldName")
    fun observeFields(categoryId: String): Flow<List<CategoryFieldDefinitionEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(category: CategoryEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertField(field: CategoryFieldDefinitionEntity)
    @Update suspend fun update(category: CategoryEntity)
    @Query("UPDATE categories SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id") suspend fun softDelete(id: String, updatedAt: Long = System.currentTimeMillis())
    @Query("DELETE FROM category_field_definitions WHERE id = :id") suspend fun deleteField(id: String)
}

@Dao
interface SyncOperationDao {
    @Query("SELECT * FROM sync_operations WHERE status IN ('PENDING', 'FAILED') ORDER BY createdAt")
    suspend fun pending(): List<SyncOperationEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun enqueue(operation: SyncOperationEntity)
    @Query("UPDATE sync_operations SET status = :status, retryCount = :retryCount WHERE id = :id") suspend fun updateStatus(id: String, status: String, retryCount: Int)
}