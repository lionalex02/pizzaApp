package com.example.pizzaapp.domain.model


data class FoodSearchResponse(
    val foods: List<FoodSearchItem>
)

data class FoodSearchItem(
    val fdcId: Long,
    val description: String,
    val dataType: String
)

data class FoodDetailsResponse(
    val foodNutrients: List<FoodNutrient>
)

data class FoodNutrient(
    val nutrient: Nutrient,
    val amount: Double
)

data class Nutrient(
    val name: String,
    val unitName: String
)