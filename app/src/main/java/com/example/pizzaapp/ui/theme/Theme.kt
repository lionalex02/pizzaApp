package com.example.pizzaapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = PizzaRedAccent,
    onPrimary = Color.White,
    secondary = BtnBlack,
    onSecondary = Color.White,
    tertiary = BtnEditGrey,
    background = PizzaBgMain,
    surface = Color.White,
    onBackground = PizzaTextPrimary,
    onSurface = PizzaTextPrimary
)

@Composable
fun PizzaAppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}