package com.example.fridgetracker.domain.model

data class Product(
    val id: String = java.util.UUID.randomUUID().toString(),
    val familyId: String = DEFAULT_FAMILY_ID,
    val name: String,
    val imageUri: String? = null,
    val weight: Double = 1.0,
    val initialQuantity: Double = 1.0,
    val currentQuantity: Double = initialQuantity,
    val weightUnit: String = "кг",
    val categoryId: String? = null,
    val barcode: String? = null,
    val addedDate: String = java.time.LocalDate.now().toString(),
    val expirationDate: String? = null,
    val decrementStep: Double = 1.0,
    val isDeleted: Boolean = false,
    val serverVersion: Long = 0
) {
    val quantity: Int get() = currentQuantity.toInt()

    companion object {
        const val DEFAULT_FAMILY_ID = "local-family"
    }
}
