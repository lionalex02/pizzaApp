package com.example.pizzaapp.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Constructor : Screen("constructor")
    object Details : Screen("details")
}