package com.example.pizzaapp.domain.model

data class FoodNutrient(
    val nutrient: Nutrient,
    val amount: Double
)

data class Nutrient(
    val name: String,
    val unitName: String
)