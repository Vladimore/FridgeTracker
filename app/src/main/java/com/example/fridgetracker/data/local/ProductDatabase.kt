package com.example.fridgetracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductEntity::class, UserEntity::class, FamilyEntity::class, FamilyMemberEntity::class,
        CategoryEntity::class, CategoryFieldDefinitionEntity::class, ProductFieldValueEntity::class,
        BarcodeProductCacheEntity::class, ShoppingListItemEntity::class, ProductActionEntity::class,
        SyncOperationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun syncOperationDao(): SyncOperationDao

    companion object {
        @Volatile private var instance: ProductDatabase? = null

        fun getInstance(context: Context): ProductDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "fridge_tracker.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
