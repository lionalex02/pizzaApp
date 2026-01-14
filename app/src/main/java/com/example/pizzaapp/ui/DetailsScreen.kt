package com.example.pizzaapp.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzaapp.ui.theme.*

@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    var scale by remember { mutableStateOf(0.8f) }
    LaunchedEffect(Unit) {
        androidx.compose.animation.core.animate(
            initialValue = 0.8f,
            targetValue = 1f,
            animationSpec = tween(600)
        ) { value, _ -> scale = value }
    }

    Scaffold(
        containerColor = PastelBackground,
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Назад", color = TextBlack)
                    }

                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlack)
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Изменить", color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                "Состав:",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold,
                color = TextGray
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(5) {
                    IngredientChip()
                }
            }

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .scale(scale), // Применяем анимацию
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(HighlightYellow, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Готовая Пицца", fontWeight = FontWeight.Bold)
                }
            }

            CenterInfo()
        }
    }
}

@Composable
fun IngredientChip() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
        )
        Text("Сыр", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        Text("50г", fontSize = 10.sp, color = TextGray)
    }
}

@Composable
fun CenterInfo() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("~560 г", style = MaterialTheme.typography.headlineMedium)
        Text("1250 ккал", color = PizzaRed, fontWeight = FontWeight.Bold)
    }
}