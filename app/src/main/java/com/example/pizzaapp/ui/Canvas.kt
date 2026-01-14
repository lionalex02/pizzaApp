package com.example.pizzaapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pizzaapp.R
import com.example.pizzaapp.domain.model.Ingredient


@Composable
fun PizzaCanvas(
    modifier: Modifier = Modifier,
    layers: List<Ingredient>
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Квадрат, как в ТЗ
    ) {
        // добавить тарелку TODO
        // Image()

        if (layers.isEmpty()) {
            Text("Pizza base is empty", Modifier.align(Alignment.Center))
        }

        layers.forEach { ingredient ->
            PizzaLayerItem(ingredient = ingredient)
        }
    }
}

@Composable
fun PizzaLayerItem(ingredient: Ingredient) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(400)
        ) + fadeIn(),
        modifier = Modifier.zIndex(ingredient.zIndex)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            //TODO поменять на картинки
            Text(
                text = ingredient.name.take(2),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}