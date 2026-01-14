package com.example.pizzaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzaapp.ui.DetailsScreen
import com.example.pizzaapp.ui.HomeScreen
import com.example.pizzaapp.ui.PizzaScreen
import com.example.pizzaapp.ui.Screen
import com.example.pizzaapp.ui.theme.PizzaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzaAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {

                    // ГЛАВНАЯ
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onCreateClick = { navController.navigate(Screen.Constructor.route) },
                            onPizzaClick = { navController.navigate(Screen.Details.route) }
                        )
                    }

                    // КОНСТРУКТОР
                    composable(Screen.Constructor.route) {
                        PizzaScreen(
                            onCancel = { navController.popBackStack() },
                            onSave = {
                                navController.navigate(Screen.Details.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }

                    // 3. ПОДРОБНЫЙ ПРОСМОТР
                    composable(Screen.Details.route) {
                        DetailsScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = {
                                navController.navigate(Screen.Constructor.route)
                            }
                        )
                    }
                }
            }
        }
    }
}