package com.example.pizzaapp.prezentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzaapp.domain.model.Category
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.network.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ConstructorState(
    val currentStage: Category = Category.BASE,
    val ingredients: List<Ingredient> = emptyList(),
    val filteredIngredients: List<Ingredient> = emptyList(),
    val addedLayers: List<Ingredient> = emptyList(),
    val totalCalories: Int = 0,
    val totalWeight: Int = 0,
    val selectedIngredientId: String? = null
)

class PizzaViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConstructorState())
    val uiState: StateFlow<ConstructorState> = _uiState.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            try {
                repository.getIngredients().collect { list ->
                    updateIngredientsList(list)
                }
            } catch (e: Exception) {
                Log.e("PizzaViewModel", "Error loading data: ${e.message}. Using fallback.")
                updateIngredientsList(getFallbackIngredients())
            }
        }
    }

    private fun updateIngredientsList(list: List<Ingredient>) {
        _uiState.value = _uiState.value.copy(
            ingredients = list,
            filteredIngredients = list.filter { it.category == Category.BASE }
        )
    }

    fun onStageSelected(category: Category) {
        val filtered = _uiState.value.ingredients.filter { it.category == category }
        _uiState.value = _uiState.value.copy(
            currentStage = category,
            filteredIngredients = filtered
        )
    }

    fun onAddIngredient(ingredient: Ingredient) {
        val currentLayers = _uiState.value.addedLayers.toMutableList()

        if (ingredient.category == Category.BASE || ingredient.category == Category.SAUCE) {
            currentLayers.removeAll { it.category == ingredient.category }
        }

        currentLayers.add(ingredient)
        val sortedLayers = currentLayers.sortedBy { it.zIndex }

        recalculateAndEmit(sortedLayers, ingredient.name)
    }

    fun onRemoveIngredient(ingredient: Ingredient) {
        val currentLayers = _uiState.value.addedLayers.toMutableList()
        currentLayers.remove(ingredient)
        recalculateAndEmit(currentLayers, null)
    }

    //Подсчет калорий
    private fun recalculateAndEmit(layers: List<Ingredient>, selectedId: String?) {
        val totalCals = layers.sumOf { it.caloriesPer100G }
        val calculatedWeight = layers.size * 100

        _uiState.value = _uiState.value.copy(
            addedLayers = layers,
            totalCalories = totalCals,
            totalWeight = calculatedWeight,
            selectedIngredientId = selectedId
        )
    }

    // Переключение слоев
    fun onSelectNextLayer() {
        val currentLayers = _uiState.value.addedLayers
        if (currentLayers.isEmpty()) return

        val currentId = _uiState.value.selectedIngredientId
        val currentIndex = currentLayers.indexOfFirst { it.name == currentId }

        if (currentIndex == -1) {
            selectIngredientAndCategory(currentLayers[0])
            return
        }
        if (currentIndex < currentLayers.lastIndex) {
            selectIngredientAndCategory(currentLayers[currentIndex + 1])
        }
    }

    fun onSelectPrevLayer() {
        val currentLayers = _uiState.value.addedLayers
        if (currentLayers.isEmpty()) return

        val currentId = _uiState.value.selectedIngredientId
        val currentIndex = currentLayers.indexOfFirst { it.name == currentId }

        if (currentIndex > 0) {
            selectIngredientAndCategory(currentLayers[currentIndex - 1])
        }
    }
    // для выделения проукта
    private fun selectIngredientAndCategory(ingredient: Ingredient) {
        onStageSelected(ingredient.category)
        _uiState.value = _uiState.value.copy(
            selectedIngredientId = ingredient.name
        )
    }

    // хардкод исправить TODO абоба
    private fun getFallbackIngredients(): List<Ingredient> {
        return listOf(
            Ingredient("Neapolitan dough", 250, 0f, Category.BASE),
            Ingredient("Wheat dough", 260, 0f, Category.BASE),
            Ingredient("Tomato Sauce", 40, 10f, Category.SAUCE),
            Ingredient("BBQ Sauce", 50, 10f, Category.SAUCE),
            Ingredient("Mozzarella", 280, 20f, Category.CHEESE),
            Ingredient("Parmesan", 300, 20f, Category.CHEESE),
            Ingredient("Pepperoni", 450, 30f, Category.MEAT),
            Ingredient("Bacon", 500, 30f, Category.MEAT),
            Ingredient("Ham", 200, 30f, Category.MEAT),
            Ingredient("Mushrooms", 20, 40f, Category.VEGGIES),
            Ingredient("Tomatoes", 15, 40f, Category.VEGGIES),
            Ingredient("Pineapple", 50, 50f, Category.EXTRA)
        )
    }
}