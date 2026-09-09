package com.example.fridgetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val imageUri: String? = null,
    val weight: Double = 1.0,
    val quantity: Int = 1,
    val expirationDate: String? = null
)
