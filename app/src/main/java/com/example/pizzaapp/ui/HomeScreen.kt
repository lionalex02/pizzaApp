package com.example.pizzaapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzaapp.ui.theme.*

@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onPizzaClick: () -> Unit
) {
    Scaffold(
        containerColor = PastelBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = PizzaRed,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(32.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Мои Пиццы",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(Modifier.height(24.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(3) { index ->
                    SavedPizzaCard(
                        name = "Пицца №${index + 1}",
                        cals = 1200 + (index * 100),
                        weight = 450 + (index * 50),
                        onClick = onPizzaClick
                    )
                }
            }
        }
    }
}

@Composable
fun SavedPizzaCard(name: String, cals: Int, weight: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(HighlightYellow) // Желтый фон вместо картинки пока
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text("Вес: ~$weight г", color = TextGray, fontSize = 14.sp)
                Text("$cals ккал", color = PizzaRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}