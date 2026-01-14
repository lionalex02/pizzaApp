package com.example.pizzaapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pizzaapp.domain.model.Category
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.prezentation.PizzaViewModel
import com.example.pizzaapp.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun PizzaScreen(
    viewModel: PizzaViewModel = koinViewModel(),
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        TopActionHeader(onCancel, onSave)
        StagesSelector(
            currentStage = state.currentStage,
            onStageSelect = { viewModel.onStageSelected(it) }
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(30.dp))

                SelectedProductTopPanel(
                    selectedItemName = state.selectedIngredientId,
                    onMinusClick = { /* TODO */ },
                    onPlusClick = { /* TODO */ }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PizzaLayout(
                        layers = state.addedLayers,
                        selectedId = state.selectedIngredientId,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    val currentIndex = state.addedLayers.indexOfFirst { it.name == state.selectedIngredientId }
                    LayerNavigationArrows(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        currentLayerIndex = currentIndex,
                        totalLayers = state.addedLayers.size,
                        canGoPrev = currentIndex > 0,
                        canGoNext = currentIndex != -1 && currentIndex < state.addedLayers.lastIndex,
                        onPrev = { viewModel.onSelectPrevLayer() },
                        onNext = { viewModel.onSelectNextLayer() }
                    )
                }

                TotalStatsBlock(
                    totalWeight = state.totalWeight,
                    totalCalories = state.totalCalories
                )

                Spacer(Modifier.height(60.dp))
            }

            IngredientsColumn(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                ingredients = state.filteredIngredients,
                addedLayers = state.addedLayers,
                selectedId = state.selectedIngredientId,
                onAdd = { viewModel.onAddIngredient(it) },
                onRemove = { viewModel.onRemoveIngredient(it) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedProductTopPanel(
    selectedItemName: String?,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selectedItemName != null) {
            Text(
                text = selectedItemName.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = PizzaRed,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee()
                    .padding(horizontal = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                IconButton(onClick = onMinusClick, modifier = Modifier.size(28.dp)) {
                    Text("-", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                }
                Text(
                    text = "100 г",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = onPlusClick, modifier = Modifier.size(28.dp)) {
                    Text("+", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                }
            }
        } else {
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
fun TotalStatsBlock(totalWeight: Int, totalCalories: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Вес: ~$totalWeight г",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        val kcalPer100 = if (totalWeight > 0) (totalCalories.toDouble() / totalWeight * 100).toInt() else 0

        Text(
            text = "ккал в 100 гр пиццы: $kcalPer100",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
fun TopActionHeader(onCancel: () -> Unit, onSave: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) { Text("Отменить", color = TextGray) }
        Button(onClick = onSave, colors = ButtonDefaults.buttonColors(containerColor = PizzaRed), shape = RoundedCornerShape(8.dp)) {
            Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StagesSelector(currentStage: Category, onStageSelect: (Category) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.KeyboardArrowLeft, null, tint = TextGray, modifier = Modifier.padding(start=8.dp))
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(Category.values()) { category ->
                val isSelected = category == currentStage
                Text(
                    text = category.name,
                    color = if (isSelected) TextBlack else TextGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onStageSelect(category) }.padding(8.dp)
                )
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.padding(end=8.dp))
    }
}

@Composable
fun PizzaLayout(layers: List<Ingredient>, selectedId: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.aspectRatio(1f)) {
        if (layers.isEmpty()) Text("Пусто", Modifier.align(Alignment.Center), color = TextGray)
        val selectedIndex = layers.indexOfFirst { it.name == selectedId }
        val visibleLayers = if (selectedId == null || selectedIndex == -1) layers else layers.take(selectedIndex + 1)
        visibleLayers.forEach { FallingIngredient(it) }
    }
}

@Composable
fun FallingIngredient(ingredient: Ingredient) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.zIndex(ingredient.zIndex).fillMaxSize()
    ) {
        Image(
            painter = painterResource(PizzaResources.getIngredientImage(ingredient.name)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LayerNavigationArrows(modifier: Modifier, currentLayerIndex: Int, totalLayers: Int, canGoPrev: Boolean, canGoNext: Boolean, onPrev: () -> Unit, onNext: () -> Unit) {
    val text = if (totalLayers > 0 && currentLayerIndex >= 0) "Слой ${currentLayerIndex + 1}/$totalLayers" else "Слои"
    val prevColor = if (canGoPrev) PizzaRed else Color.LightGray
    val nextColor = if (canGoNext) PizzaRed else Color.LightGray
    Row(
        modifier = modifier.background(Color.White, RoundedCornerShape(20.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev, enabled = canGoPrev, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = prevColor) }
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = onNext, enabled = canGoNext, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.KeyboardArrowRight, null, tint = nextColor) }
    }
}

@Composable
fun IngredientsColumn(
    modifier: Modifier,
    ingredients: List<Ingredient>,
    addedLayers: List<Ingredient>,
    selectedId: String?,
    onAdd: (Ingredient) -> Unit,
    onRemove: (Ingredient) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(selectedId) {
        if (selectedId != null) {
            val index = ingredients.indexOfFirst { it.name == selectedId }
            if (index != -1) listState.animateScrollToItem(index, scrollOffset = -100)
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier.background(Color.White),
        contentPadding = PaddingValues(top = 0.dp, bottom = 80.dp)
    ) {
        items(ingredients) { ingredient ->
            IngredientCard(
                ingredient = ingredient,
                isAdded = addedLayers.any { it.name == ingredient.name },
                isFocused = ingredient.name == selectedId,
                onAdd = { onAdd(ingredient) },
                onRemove = { onRemove(ingredient) }
            )
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    isAdded: Boolean,
    isFocused: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val bgColor = when {
        isFocused -> HighlightGreen
        isAdded -> HighlightYellow
        else -> Color.White
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp).height(110.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = PizzaResources.getIngredientImage(ingredient.name)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp).background(Color.Transparent)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ingredient.caloriesPer100G} ккал",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray,
                    fontSize = 10.sp
                )
            }
            Box(modifier = Modifier.width(1.dp).fillMaxHeight(0.8f).background(Color.LightGray.copy(alpha=0.5f)))
            Column(
                modifier = Modifier.width(32.dp).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isAdded) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = PizzaRedDark, modifier = Modifier.size(24.dp).clickable { onRemove() })
                } else {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = ButtonBlack, modifier = Modifier.size(28.dp).clickable { onAdd() })
                }
            }
        }
    }
}