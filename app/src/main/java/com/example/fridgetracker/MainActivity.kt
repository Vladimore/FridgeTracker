package com.example.fridgetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fridgetracker.data.local.ProductDatabase
import com.example.fridgetracker.data.repository.ProductRepository
import com.example.fridgetracker.data.repository.CategoryRepository
import com.example.fridgetracker.data.settings.UserSettingsRepository
import com.example.fridgetracker.ui.FridgeTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ProductRepository(
            ProductDatabase.getInstance(applicationContext).productDao(),
            ProductDatabase.getInstance(applicationContext).syncOperationDao()
        )
        val categoryRepository = CategoryRepository(
            ProductDatabase.getInstance(applicationContext).categoryDao()
        )

        setContent {
            FridgeTrackerApp(repository, categoryRepository, UserSettingsRepository(applicationContext))
        }
    }
}
