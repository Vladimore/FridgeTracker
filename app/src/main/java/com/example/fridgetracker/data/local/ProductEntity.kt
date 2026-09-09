package com.example.fridgetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val name: String,
    val imageUri: String? = null,
    val weight: Double = 1.0,
    val initialQuantity: Double = 1.0,
    val currentQuantity: Double = 1.0,
    val weightUnit: String = "кг",
    val categoryId: String? = null,
    val barcode: String? = null,
    val addedDate: String,
    val expirationDate: String? = null,
    val decrementStep: Double = 1.0,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val serverVersion: Long = 0
)
