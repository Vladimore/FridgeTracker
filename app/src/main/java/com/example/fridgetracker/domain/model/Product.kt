package com.example.fridgetracker.domain.model

data class Product(
    val id: Long = 0,
    val name: String,
    val imageUri: String? = null,
    val weight: Double = 1.0,
    val quantity: Int = 1,
    val expirationDate: String? = null
)
