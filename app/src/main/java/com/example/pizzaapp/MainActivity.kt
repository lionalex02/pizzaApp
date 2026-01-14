package com.example.pizzaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzaapp.domain.model.PizzaRecipe
import com.example.pizzaapp.ui.DetailsScreen
import com.example.pizzaapp.ui.HomeScreen
import com.example.pizzaapp.ui.PizzaScreen
import com.example.pizzaapp.ui.Screen
import com.example.pizzaapp.ui.theme.PizzaAppTheme
import org.koin.androidx.compose.koinViewModel

object MockStorage {
    var selectedRecipe: PizzaRecipe? = null
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PizzaAppTheme {
                val navController = rememberNavController()
                val savedPizzas = remember { mutableStateListOf<PizzaRecipe>() }
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    // ГЛАВНАЯ
                    composable("home") {
                        HomeScreen(
                            pizzas = savedPizzas,
                            onNewPizza = {
                                MockStorage.selectedRecipe = null
                                navController.navigate("constructor")
                            },
                            onOpenDetails = { recipe ->
                                MockStorage.selectedRecipe = recipe
                                navController.navigate("details")
                            }
                        )
                    }

                    // КОНСТРУКТОР
                    composable("constructor") {
                        val viewModel: com.example.pizzaapp.prezentation.PizzaViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()

                        LaunchedEffect(Unit) {
                            if (MockStorage.selectedRecipe != null) {
                                viewModel.loadRecipe(MockStorage.selectedRecipe!!)
                                MockStorage.selectedRecipe = null
                            } else {
                                viewModel.resetState()
                            }
                        }

                        PizzaScreen(
                            onCancel = { navController.popBackStack() },
                            onSave = { name ->
                                if (state.addedLayers.isNotEmpty()) {
                                    val existingId = state.id
                                    val newRecipe = PizzaRecipe(
                                        id = existingId ?: java.util.UUID.randomUUID().toString(),
                                        name = name,
                                        ingredients = state.addedLayers,
                                        totalCalories = state.totalCalories,
                                        totalWeight = state.totalWeight
                                    )

                                    if (existingId != null) {
                                        val index = savedPizzas.indexOfFirst { it.id == existingId }
                                        if (index != -1) {
                                            savedPizzas[index] = newRecipe
                                        } else {
                                            savedPizzas.add(newRecipe)
                                        }
                                    } else {
                                        savedPizzas.add(newRecipe)
                                    }

                                    navController.popBackStack("home", inclusive = false)
                                }
                            }
                        )
                    }

                    // ПОДРОБНЫЙ ПРОСМОТР
                    composable("details") {
                        DetailsScreen(
                            recipe = MockStorage.selectedRecipe,
                            onBack = { navController.popBackStack() },
                            onEdit = {
                                navController.navigate("constructor")
                            }
                        )
                    }
                }
            }
        }
    }
}