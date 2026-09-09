package com.example.fridgetracker.data.repository

import com.example.fridgetracker.data.local.ProductDao
import com.example.fridgetracker.data.local.ProductEntity
import com.example.fridgetracker.data.local.ProductActionEntity
import com.example.fridgetracker.data.local.SyncOperationDao
import com.example.fridgetracker.data.local.SyncOperationEntity
import com.example.fridgetracker.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProductRepository(
    private val dao: ProductDao,
    private val syncDao: SyncOperationDao
) {
    fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun addProduct(product: Product) {
        val now = System.currentTimeMillis()
        val saved = product.copy(
            id = product.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        ).toEntity(now)
        dao.insert(saved)
        record(saved, "ADDED", null, saved.currentQuantity, now)
    }

    suspend fun updateProduct(product: Product) {
        val now = System.currentTimeMillis()
        dao.update(product.toEntity(now))
        record(product.toEntity(now), "UPDATED", null, product.currentQuantity, now)
    }

    suspend fun deleteProduct(product: Product) {
        val now = System.currentTimeMillis()
        dao.softDelete(product.id, now)
        record(product.toEntity(now), "DELETED", product.currentQuantity, 0.0, now)
    }

    suspend fun decrementProduct(product: Product) {
        val now = System.currentTimeMillis()
        val next = (product.currentQuantity - product.decrementStep).coerceAtLeast(0.0)
        dao.updateQuantity(product.id, next, now)
        record(product.toEntity(now), "DECREMENTED", product.currentQuantity, next, now)
    }

    private suspend fun record(product: ProductEntity, action: String, before: Double?, after: Double?, now: Long) {
        syncDao.enqueue(
            SyncOperationEntity(
                id = UUID.randomUUID().toString(),
                entityId = product.id,
                entityType = "PRODUCT",
                operationType = action,
                payload = product.toString(),
                createdAt = now
            )
        )
    }

    private fun ProductEntity.toDomain() = Product(id, familyId, name, imageUri, weight, initialQuantity, currentQuantity, weightUnit, categoryId, barcode, addedDate, expirationDate, decrementStep, isDeleted, serverVersion)
    private fun Product.toEntity(now: Long) = ProductEntity(id, familyId, name, imageUri, weight, initialQuantity, currentQuantity, weightUnit, categoryId, barcode, addedDate, expirationDate, decrementStep, now, now, isDeleted, serverVersion)
}
