package com.example.pizzaapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.ui.theme.BtnBlack
import com.example.pizzaapp.ui.theme.ItemSelectedBg
import com.example.pizzaapp.ui.theme.PizzaRedDark
import com.example.pizzaapp.ui.theme.PizzaTextPrimary

@Composable
fun IngredientList(
    modifier: Modifier = Modifier,
    ingredients: List<Ingredient>,
    addedIngredients: List<Ingredient>,
    onAdd: (Ingredient) -> Unit,
    onRemove: (Ingredient) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(ingredients) { ingredient ->
            val isAdded = addedIngredients.any { it.name == ingredient.name }

            IngredientItem(
                ingredient = ingredient,
                isAdded = isAdded,
                onAdd = { onAdd(ingredient) },
                onRemove = { onRemove(ingredient) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IngredientItem(
    ingredient: Ingredient,
    isAdded: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val bgColor = if (isAdded) ItemSelectedBg else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ingredient.name.take(2), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PizzaTextPrimary,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
        }

        if (isAdded) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = PizzaRedDark
                )
            }
        } else {
            IconButton(
                onClick = onAdd,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = BtnBlack
                )
            }
        }
    }
}