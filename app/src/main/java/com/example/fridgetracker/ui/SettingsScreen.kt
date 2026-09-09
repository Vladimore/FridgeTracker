package com.example.fridgetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    gridMode: Boolean,
    onGridModeChange: (Boolean) -> Unit,
    onCategoriesClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Настройки",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            Modifier.height(20.dp)
        )

        SettingRow(
            "Темная тема",
            darkTheme,
            onDarkThemeChange
        )

        SettingRow(
            "Включить уведомления",
            notificationsEnabled,
            onNotificationsChange
        )

        SettingRow("Вид карточек: две колонки", gridMode, onGridModeChange)

        OutlinedButton(onClick = onCategoriesClick, modifier = Modifier.fillMaxWidth()) {
            Text("Категории и поля")
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
