package com.example.fridgetracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fridgetracker.data.local.CategoryEntity
import com.example.fridgetracker.data.repository.CategoryRepository
import kotlinx.coroutines.launch

private val FieldTypes = listOf("TEXT", "NUMBER", "DECIMAL", "BOOLEAN", "DATE", "COLOR", "SELECT")

@Composable
fun CategoryScreen(repository: CategoryRepository, onBack: () -> Unit) {
    val categories by repository.observeCategories("local-family").collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showFieldDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onBack) { Text("Назад") }
            Button(onClick = { showCategoryDialog = true }) { Text("Добавить категорию") }
        }
        Spacer(Modifier.height(12.dp))
        Text("Категории")
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories, key = { it.id }) { category ->
                val fields by repository.observeFields(category.id).collectAsState(initial = emptyList())
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(category.name)
                            OutlinedButton(onClick = { scope.launch { repository.deleteCategory(category) } }) { Text("Удалить") }
                        }
                        fields.forEach { field ->
                            Text("${field.fieldName} (${field.fieldType})")
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(onClick = { selectedCategory = category; showFieldDialog = true }) { Text("Добавить поле") }
                        }
                    }
                }
            }
        }
    }

    if (showCategoryDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Новая категория") },
            text = { OutlinedTextField(name, { name = it }, label = { Text("Название") }, singleLine = true) },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) scope.launch { repository.addCategory(name); showCategoryDialog = false }
                }) { Text("Создать") }
            },
            dismissButton = { OutlinedButton(onClick = { showCategoryDialog = false }) { Text("Отмена") } }
        )
    }

    if (showFieldDialog && selectedCategory != null) {
        var name by remember { mutableStateOf("") }
        var type by remember { mutableStateOf(FieldTypes.first()) }
        var expanded by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showFieldDialog = false },
            title = { Text("Новое поле") },
            text = {
                Column {
                    OutlinedTextField(name, { name = it }, label = { Text("Название поля") }, singleLine = true)
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Тип: $type")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        FieldTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { type = option; expanded = false }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) scope.launch { repository.addField(selectedCategory!!.id, name, type, 0); showFieldDialog = false }
                }) { Text("Добавить") }
            },
            dismissButton = { OutlinedButton(onClick = { showFieldDialog = false }) { Text("Отмена") } }
        )
    }
}