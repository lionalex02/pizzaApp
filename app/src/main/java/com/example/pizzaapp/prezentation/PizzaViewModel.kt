package com.example.pizzaapp.prezentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pizzaapp.domain.model.Category
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.network.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


data class ConstructorState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val id: String? = null,
    val name: String = "",
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

    fun resetState() {
        _uiState.value = ConstructorState(
            id = null,
            name = "",
            ingredients = _uiState.value.ingredients,
            filteredIngredients = _uiState.value.ingredients.filter { it.category == Category.BASE }
        )
    }

    fun loadRecipe(recipe: com.example.pizzaapp.domain.model.PizzaRecipe) {
        _uiState.value = _uiState.value.copy(
            id = recipe.id,
            name = recipe.name,
            addedLayers = recipe.ingredients,
            totalCalories = recipe.totalCalories,
            totalWeight = recipe.totalWeight
        )
    }

    fun loadIngredients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val list = repository.getIngredients().first()

                _uiState.value = _uiState.value.copy(
                    ingredients = list,
                    filteredIngredients = list.filter { it.category == Category.BASE },
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("PizzaViewModel", "Error loading data", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки ингредиентов"
                )
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

        currentLayers.add(ingredient.copy(weightGrams = 50))
        val sortedLayers = currentLayers.sortedBy { it.zIndex }

        recalculateAndEmit(sortedLayers, ingredient.name)
    }
    fun updateIngredientWeight(delta: Int) {
        val selectedId = _uiState.value.selectedIngredientId ?: return
        val currentLayers = _uiState.value.addedLayers.toMutableList()

        val index = currentLayers.indexOfFirst { it.name == selectedId }
        if (index != -1) {
            val item = currentLayers[index]
            val newWeight = (item.weightGrams + delta).coerceIn(50, 500000)

            currentLayers[index] = item.copy(weightGrams = newWeight)

            recalculateAndEmit(currentLayers, selectedId)
        }
    }

    fun onRemoveIngredient(ingredient: Ingredient) {
        val currentLayers = _uiState.value.addedLayers.toMutableList()
        currentLayers.remove(ingredient)
        recalculateAndEmit(currentLayers, null)
    }

    //Подсчет калорий
    private fun recalculateAndEmit(layers: List<Ingredient>, selectedId: String?) {
        val totalWeight = layers.sumOf { it.weightGrams }
        val totalCals = layers.sumOf { (it.caloriesPer100G * it.weightGrams) / 100 }

        _uiState.value = _uiState.value.copy(
            addedLayers = layers,
            totalCalories = totalCals,
            totalWeight = totalWeight,
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
//    private suspend fun getFallbackIngredients(): List<Ingredient> {
//
//        return repository.getIngredients().first()
//        return listOf(
//            Ingredient("Neapolitan dough", 250, 0f, Category.BASE),
//            Ingredient("Wheat dough", 260, 0f, Category.BASE),
//            Ingredient("Tomato Sauce", 40, 10f, Category.SAUCE),
//            Ingredient("BBQ Sauce", 50, 10f, Category.SAUCE),
//            Ingredient("Mozzarella", 280, 20f, Category.CHEESE),
//            Ingredient("Parmesan", 300, 20f, Category.CHEESE),
//            Ingredient("Pepperoni", 450, 30f, Category.MEAT),
//            Ingredient("Bacon", 500, 30f, Category.MEAT),
//            Ingredient("Ham", 200, 30f, Category.MEAT),
//            Ingredient("Mushrooms", 20, 40f, Category.VEGGIES),
//            Ingredient("Tomatoes", 15, 40f, Category.VEGGIES),
//            Ingredient("Pineapple", 50, 50f, Category.EXTRA)
//        )
    //}
}