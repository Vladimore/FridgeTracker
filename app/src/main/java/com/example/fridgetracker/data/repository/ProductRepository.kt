package com.example.fridgetracker.data.repository

import com.example.fridgetracker.data.local.ProductDao
import com.example.fridgetracker.data.local.ProductEntity
import com.example.fridgetracker.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(private val dao: ProductDao) {
    fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun addProduct(product: Product): Long = dao.insert(product.toEntity())
    suspend fun updateProduct(product: Product) = dao.update(product.toEntity())
    suspend fun deleteProduct(product: Product) = dao.delete(product.toEntity())

    private fun ProductEntity.toDomain() = Product(id, name, imageUri, weight, quantity, expirationDate)
    private fun Product.toEntity() = ProductEntity(id, name, imageUri, weight, quantity, expirationDate)
}
