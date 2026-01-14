package com.example.pizzaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.domain.model.PizzaRecipe
import com.example.pizzaapp.ui.theme.*

@Composable
fun DetailsScreen(
    recipe: PizzaRecipe?,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    if (recipe == null) return

    Scaffold(
        containerColor = PastelBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = TextBlack)
                }
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = PizzaRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Изменить", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                items(recipe.ingredients) { ingredient ->
                    ReadOnlyIngredientChip(ingredient)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                PizzaThumbnail(
                    layers = recipe.ingredients,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Вес: ~${recipe.totalWeight} г",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextBlack
                )
                Text(
                    text = "${recipe.totalCalories} ккал",
                    style = MaterialTheme.typography.titleMedium,
                    color = PizzaRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReadOnlyIngredientChip(ingredient: Ingredient) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.size(60.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = PizzaResources.getIngredientImage(ingredient.name)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.labelSmall,
            color = TextBlack,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 12.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}