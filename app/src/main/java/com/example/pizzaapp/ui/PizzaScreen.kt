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
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pizzaapp.domain.model.Category
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.prezentation.ConstructorState
import com.example.pizzaapp.prezentation.PizzaViewModel
import com.example.pizzaapp.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun PizzaScreen(
    viewModel: PizzaViewModel = koinViewModel(),
    onCancel: () -> Unit,
    onSave: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var pizzaNameInput by remember { mutableStateOf("") }


    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Выход") },
            text = { Text("Несохраненные изменения будут потеряны.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onCancel()
                    }
                ) { Text("Выйти", color = PizzaRed) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Отмена", color = TextBlack)
                }
            },
            containerColor = Color.White
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Сохранение пиццы") },
            text = {
                Column {
                    Text("Введите название:")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pizzaNameInput,
                        onValueChange = { pizzaNameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PizzaRed,
                            cursorColor = PizzaRed
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        onSave(pizzaNameInput.ifBlank { "Пицца без названия" })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PizzaRed)
                ) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Отмена", color = TextBlack)
                }
            },
            containerColor = Color.White
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PizzaRed
                )
            }

            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error!!, color = PizzaRed)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.loadIngredients() },
                        colors = ButtonDefaults.buttonColors(containerColor = PizzaRed)
                    ) { Text("Повторить") }
                }
            }

            else -> {
                PizzaContent(
                    state = state,
                    onCancel = { showExitDialog = true },
                    onSave = {
                        pizzaNameInput = state.name
                        showSaveDialog = true
                    },
                    onStageSelect = viewModel::onStageSelected,
                    onAddIngredient = viewModel::onAddIngredient,
                    onRemoveIngredient = viewModel::onRemoveIngredient,
                    onPrevLayer = viewModel::onSelectPrevLayer,
                    onNextLayer = viewModel::onSelectNextLayer,
                    onWeightMinus = { viewModel.updateIngredientWeight(-50) },
                    onWeightPlus = { viewModel.updateIngredientWeight(50) }
                )
            }
        }
    }
}

@Composable
fun PizzaContent(
    state: ConstructorState,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onStageSelect: (Category) -> Unit,
    onAddIngredient: (Ingredient) -> Unit,
    onRemoveIngredient: (Ingredient) -> Unit,
    onPrevLayer: () -> Unit,
    onNextLayer: () -> Unit,
    onWeightMinus: () -> Unit,
    onWeightPlus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelBackground)
    ) {
        TopActionHeader(onCancel, onSave)

        StagesSelector(
            currentStage = state.currentStage,
            onStageSelect = onStageSelect
        )

        Row(Modifier.weight(1f)) {

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val selected = state.addedLayers.find { it.name == state.selectedIngredientId }
                val weight = selected?.weightGrams ?: 100

                SelectedProductTopPanel(
                    selectedItemName = state.selectedIngredientId,
                    weight = weight,
                    onMinusClick = onWeightMinus,
                    onPlusClick = onWeightPlus
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    PizzaLayout(
                        layers = state.addedLayers,
                        selectedId = state.selectedIngredientId,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }

                val index = state.addedLayers.indexOfFirst { it.name == state.selectedIngredientId }
                LayerNavigationArrows(
                    modifier = Modifier.padding(vertical = 8.dp),
                    currentLayerIndex = index,
                    totalLayers = state.addedLayers.size,
                    canGoPrev = true,
                    canGoNext = true,
                    onPrev = onPrevLayer,
                    onNext = onNextLayer
                )

                TotalStatsBlock(state.totalWeight, state.totalCalories)
                Spacer(Modifier.height(100.dp))
            }

            IngredientsColumn(
                modifier = Modifier.weight(0.4f),
                ingredients = state.filteredIngredients,
                addedLayers = state.addedLayers,
                selectedId = state.selectedIngredientId,
                onAdd = onAddIngredient,
                onRemove = onRemoveIngredient
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedProductTopPanel(
    selectedItemName: String?,
    weight: Int,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selectedItemName != null) {
            Text(
                text = selectedItemName.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
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
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(onClick = onMinusClick, modifier = Modifier.size(32.dp)) {
                    Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                }
                Text(
                    text = "$weight г",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = onPlusClick, modifier = Modifier.size(32.dp)) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                }
            }
        } else {
            Text(
                text = "ВЫБЕРИТЕ ПРОДУКТ",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) { Text("Отменить", color = TextGray) }
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = PizzaRed),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StagesSelector(currentStage: Category, onStageSelect: (Category) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.KeyboardArrowLeft, null, tint = TextGray, modifier = Modifier.padding(start = 8.dp))
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
                    modifier = Modifier
                        .clickable { onStageSelect(category) }
                        .padding(8.dp)
                )
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.padding(end = 8.dp))
    }
}

@Composable
fun PizzaLayout(layers: List<Ingredient>, selectedId: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.aspectRatio(1f)) {
        if (layers.isEmpty()) Text("Пусто", Modifier.align(Alignment.Center), color = TextGray)

        val selectedIndex = layers.indexOfFirst { it.name == selectedId }

        val visibleLayers = if (selectedId == null || selectedIndex == -1) {
            layers
        } else {
            layers.take(selectedIndex + 1)
        }

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
        modifier = Modifier
            .zIndex(ingredient.zIndex)
            .fillMaxSize()
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
fun LayerNavigationArrows(
    modifier: Modifier,
    currentLayerIndex: Int,
    totalLayers: Int,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val text = if (totalLayers > 0 && currentLayerIndex >= 0) "Слой ${currentLayerIndex + 1}/$totalLayers" else "Слои"
    Row(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev, enabled = canGoPrev, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.KeyboardArrowLeft, null, tint = if(canGoPrev) PizzaRed else Color.LightGray)
        }
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = onNext, enabled = canGoNext, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.KeyboardArrowRight, null, tint = if(canGoNext) PizzaRed else Color.LightGray)
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
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
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Transparent)
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
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.8f)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
            Column(
                modifier = Modifier
                    .width(32.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isAdded) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = PizzaRedDark,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onRemove() }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = ButtonBlack,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onAdd() }
                    )
                }
            }
        }
    }
}