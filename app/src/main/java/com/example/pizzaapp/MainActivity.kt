package com.example.pizzaapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzaapp.domain.model.PizzaRecipe
import com.example.pizzaapp.ui.DetailsScreen
import com.example.pizzaapp.ui.HomeScreen
import com.example.pizzaapp.ui.PizzaScreen
import com.example.pizzaapp.ui.theme.PizzaAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
                val context = LocalContext.current

                val savedPizzas = remember {
                    mutableStateListOf<PizzaRecipe>().apply {
                        addAll(loadPizzas(context))
                    }
                }

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
                            },
                            onDeletePizza = { recipe ->
                                savedPizzas.remove(recipe)
                                savePizzas(context, savedPizzas)
                            }
                        )
                    }

                    // КОНСТРУКТОР
                    composable("constructor") {
                        val viewModel: com.example.pizzaapp.prezentation.PizzaViewModel = koinViewModel()
                        val state by viewModel.uiState.collectAsState()

                        LaunchedEffect(Unit) {
                            val recipeToEdit = MockStorage.selectedRecipe
                            if (recipeToEdit != null) {
                                viewModel.loadRecipe(recipeToEdit)
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

                                    savePizzas(context, savedPizzas)
                                    navController.popBackStack("home", inclusive = false)
                                }
                            }
                        )
                    }

                    // ПОДРОБНЫЙ ПРОСМОТР
                    composable("details") {
                        val recipe = requireNotNull(MockStorage.selectedRecipe) {
                            "Попытка открыть экран деталей без выбранного рецепта (MockStorage.selectedRecipe is null)"
                        }

                        DetailsScreen(
                            recipe = recipe,
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

    private fun savePizzas(context: Context, pizzas: List<PizzaRecipe>) {
        val json = Gson().toJson(pizzas)
        context.getSharedPreferences("pizza_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("saved_pizzas", json)
            .apply()
    }

    private fun loadPizzas(context: Context): List<PizzaRecipe> {
        val json = context.getSharedPreferences("pizza_prefs", Context.MODE_PRIVATE)
            .getString("saved_pizzas", null) ?: return emptyList()
        val type = object : TypeToken<List<PizzaRecipe>>() {}.type
        return Gson().fromJson(json, type)
    }
}