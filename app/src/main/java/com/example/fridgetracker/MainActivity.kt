package com.example.fridgetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fridgetracker.data.local.ProductDatabase
import com.example.fridgetracker.data.repository.ProductRepository
import com.example.fridgetracker.ui.FridgeTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ProductRepository(
            ProductDatabase.getInstance(applicationContext).productDao()
        )

        setContent {
            FridgeTrackerApp(repository)
        }
    }
}
