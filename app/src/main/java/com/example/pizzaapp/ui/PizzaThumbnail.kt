package com.example.pizzaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import com.example.pizzaapp.domain.model.Ingredient

@Composable
fun PizzaThumbnail(
    layers: List<Ingredient>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        layers.forEach { ingredient ->
            Image(
                painter = painterResource(id = PizzaResources.getIngredientImage(ingredient.name)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(ingredient.zIndex)
            )
        }
    }
}