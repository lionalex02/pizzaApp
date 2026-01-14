package com.example.pizzaapp.domain.model

data class PizzaRecipe(
    val id: String,
    val name: String,
    val ingredients: List<Ingredient>,
    val totalCalories: Int,
    val totalWeight: Int
)