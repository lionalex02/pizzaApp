package com.example.pizzaapp.domain.model

import androidx.compose.ui.text.font.FontWeight

data class Ingredient(
    val name: String,
    val caloriesPer100G : Int,
    val zIndex: Float,
    val category: Category,
    val weightGrams: Int = 50
)