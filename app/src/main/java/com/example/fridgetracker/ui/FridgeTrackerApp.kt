package com.example.fridgetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.fridgetracker.data.repository.ProductRepository
import com.example.fridgetracker.ui.theme.FridgeTrackerTheme

@Composable
fun FridgeTrackerApp(repository: ProductRepository) {
    var darkTheme by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    FridgeTrackerTheme(darkTheme) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavItem(
                        navController,
                        "home",
                        Icons.Default.Home,
                        "Главная"
                    )

                    NavItem(
                        navController,
                        "cart",
                        Icons.Default.ShoppingCart,
                        "Корзина"
                    )

                    NavItem(
                        navController,
                        "settings",
                        Icons.Default.Settings,
                        "Настройки"
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") {
                    HomeScreen(repository)
                }

                composable("cart") {
                    CartScreen()
                }

                composable("settings") {
                    SettingsScreen(
                        darkTheme,
                        { darkTheme = it },
                        notificationsEnabled,
                        { notificationsEnabled = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    navController: NavHostController,
    route: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    val current by navController.currentBackStackEntryAsState()

    NavigationBarItem(
        selected = current?.destination?.route == route,
        onClick = {
            navController.navigate(route) {
                launchSingleTop = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                restoreState = true
            }
        },
        icon = {
            Icon(icon, contentDescription = label)
        },
        label = {
            Text(label)
        }
    )
}
