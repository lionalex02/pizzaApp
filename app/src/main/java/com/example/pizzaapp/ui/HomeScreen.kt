package com.example.pizzaapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pizzaapp.domain.model.PizzaRecipe
import com.example.pizzaapp.ui.PizzaThumbnail
import com.example.pizzaapp.ui.theme.*

@Composable
fun HomeScreen(
    pizzas: List<PizzaRecipe>,
    onNewPizza: () -> Unit,
    onOpenDetails: (PizzaRecipe) -> Unit
) {
    Scaffold(
        containerColor = PastelBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewPizza,
                containerColor = PizzaRed,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "Мои Пиццы",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = TextBlack,
                modifier = Modifier.padding(24.dp)
            )

            if (pizzas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет сохраненных пицц", color = TextGray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pizzas) { pizza ->
                        PizzaCard(pizza = pizza, onClick = { onOpenDetails(pizza) })
                    }
                }
            }
        }
    }
}

@Composable
fun PizzaCard(pizza: PizzaRecipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                PizzaThumbnail(
                    layers = pizza.ingredients,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pizza.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "~${pizza.totalWeight} г",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray
                )
                Text(
                    text = "${pizza.totalCalories} ккал",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = PizzaRed
                )
            }
        }
    }
}