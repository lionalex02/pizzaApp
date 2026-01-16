package com.example.pizzaapp.network.repository

import com.example.pizzaapp.BuildConfig
import com.example.pizzaapp.data.local.JsonCacheManager
import com.example.pizzaapp.domain.model.Category
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.network.api.Api
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow

class Repository(
    private val api: Api,
    private val cacheManager: JsonCacheManager
) {
    val apiKey = BuildConfig.API_KEY
//    suspend fun getCalories(name: String) :Int {
//        val id = api.searchFood(name, 1, apiKey)
//            .foods.first().fdcId
//        val calories = api.getFoodDetails(id, apiKey).foodNutrients.firstOrNull {
//            it.nutrient.name == "Energy" && it.nutrient.unitName == "kcal"
//        }?.amount?.toInt() ?: 0
//        return calories
//    }
    private val semaphore = Semaphore(10) //Количество процессов отправленных одновременно

    val connectionError = MutableSharedFlow<Boolean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )


    suspend fun getCalories(name: String): Int {
        val cachedValue = cacheManager.getCalories(name)
        if (cachedValue != null && cachedValue > 0) {
            return cachedValue
        }

        return semaphore.withPermit {
            try {
                delay(1000)

                val response = api.searchFood(name, 5, apiKey,  dataType = listOf("Foundation", "SR Legacy", "Survey (FNDDS)"))
                if (response.foods.isEmpty()) return 0

                val provenFood = response.foods.firstOrNull {
                    it.dataType == "SR Legacy" || it.dataType == "Foundation" || it.dataType == "Survey (FNDDS)"
                } ?: response.foods.first()
                val id = provenFood.fdcId
                val details = api.getFoodDetails(id, apiKey)

                val cals = details.foodNutrients.firstOrNull {
                   //it.nutrient.name == "Energy" && it.nutrient.unitName == "kcal"
                    it.nutrient.name.contains("Energy", ignoreCase = true) && it.nutrient.unitName.equals("kcal", ignoreCase = true)
                }?.amount?.toInt() ?: 0

                if (cals > 0) {
                    cacheManager.saveCalories(name, cals)
                }
                return cals
            } catch (e: Exception) {
                e.printStackTrace()
                connectionError.emit(true)
                 0
            }
        }
    }


    fun getIngredients(): Flow<List<Ingredient>> {
        return flow {
            emit(loadIngredients())
        }.catch { e ->
            if (e is SocketTimeoutException) {
                emit(fallbackIngredients())
            } else {
                throw e
            }
        }
    }
    private suspend fun loadIngredients():List<Ingredient>{
        return coroutineScope {
            val neapolitanCal = async {getCalories("Neapolitan dough")}
            val wheatCal = async {getCalories("Wheat dough")}
            val ryeCal = async {getCalories("Rye dough")}
            val baconCal = async {getCalories("Bacon")}
            val hamCal = async {getCalories("Ham")}
            val chickenCal = async {getCalories("Chicken fillet")}
            val salamiCal = async {getCalories("Salami")}
            val pepperoniCal = async {getCalories("pepperoni")}
            val mushroomsCal = async {getCalories("Mushrooms")}
            val olivesCal = async {getCalories("olives")}
            val jalapenoCal = async {getCalories("jalapeno")}
            val tomatoesCal = async {getCalories("tomatoes")}
            val picklesCal = async {getCalories("pickles")}
            val ketchupCal = async {getCalories("ketchup")}
            val mayonnaiseCal = async {getCalories("mayonnaise")}
            val barbecueCal = async {getCalories("barbecue sauce")}
            val mozzarellaCal = async {getCalories("mozzarella Cheese")}
            val cheddarCal = async {getCalories("Cheddar cheese")}
            val goudaCal = async {getCalories("Gouda cheese")}
            val parmesanCal = async {getCalories("Parmesan cheese")}
            val pineappleCal= async {getCalories("pineapple")}
            val dillCal = async {getCalories("dill")}

                //neapolitanCal.await()
        listOf(
            Ingredient("Неаполитанское тесто", neapolitanCal.await(), 0f, Category.BASE),
            Ingredient("Пшеничное тесто",wheatCal.await(), 0f, Category.BASE),
            Ingredient("Ржаное тесто", ryeCal.await(), 0f, Category.BASE),

            Ingredient("Бекон", baconCal.await(), 4f, Category.MEAT),
            Ingredient("Ветчина", hamCal.await(), 2f, Category.MEAT),
            Ingredient("Куриное филе", chickenCal.await(), 3f, Category.MEAT),
            Ingredient("Салями", salamiCal.await(), 2f, Category.MEAT),
            Ingredient("Колбаса пепперони", pepperoniCal.await(), 2f, Category.MEAT),

            Ingredient("Шампиньоны", mushroomsCal.await(), 5f, Category.VEGGIES),
            Ingredient("Оливки", olivesCal.await(), 8f, Category.VEGGIES),
            Ingredient("Халапеньо", jalapenoCal.await(), 8f, Category.VEGGIES),
            Ingredient("Томаты", tomatoesCal.await(), 5f, Category.VEGGIES),
            Ingredient("Маринованные огурцы", picklesCal.await(), 6f, Category.VEGGIES),

            //ketchupCal.await()
            Ingredient("Томатный соус", ketchupCal.await(), 1f, Category.SAUCE),
            Ingredient("Майонез", mayonnaiseCal.await(), 1f, Category.SAUCE),
            Ingredient("Соус Барбекю", barbecueCal.await(), 1f, Category.SAUCE),

            Ingredient("Сыр Моцарелла", mozzarellaCal.await(), 9f, Category.CHEESE),
            Ingredient("Сыр Чеддер", cheddarCal.await(), 9f, Category.CHEESE),
            Ingredient("Сыр Гауда", goudaCal.await(), 9f, Category.CHEESE),
            Ingredient("Сыр Пармезан", parmesanCal.await(), 9f, Category.CHEESE),

            Ingredient("Ананас", pineappleCal.await(), 7f, Category.EXTRA),
            Ingredient("Зелень", dillCal.await(), 10f, Category.EXTRA),
        )}
    }

    private fun fallbackIngredients(): List<Ingredient>{
        return listOf(
            Ingredient("Неаполитанское тесто", 0, 0f, Category.BASE),
            Ingredient("Пшеничное тесто",0, 0f, Category.BASE),
            Ingredient("Ржаное тесто", 0, 0f, Category.BASE),

            Ingredient("Бекон", 0, 4f, Category.MEAT),
            Ingredient("Ветчина", 0, 2f, Category.MEAT),
            Ingredient("Куриное филе", 0, 3f, Category.MEAT),
            Ingredient("Колбаса пепперони", 0, 2f, Category.MEAT),
            Ingredient("Салями", 0, 2f, Category.MEAT),

            Ingredient("Шампиньоны", 0, 5f, Category.VEGGIES),
            Ingredient("Оливки", 0, 8f, Category.VEGGIES),
            Ingredient("Халапеньо", 0, 8f, Category.VEGGIES),
            Ingredient("Томаты", 0, 5f, Category.VEGGIES),
            Ingredient("Маринованные огурцы", 0, 6f, Category.VEGGIES),

            Ingredient("Кетчуп", 0, 1f, Category.SAUCE),
            Ingredient("Майонез", 0, 1f, Category.SAUCE),
            Ingredient("Соус Барбекю", 0, 1f, Category.SAUCE),

            Ingredient("Сыр Моцарелла", 0, 9f, Category.CHEESE),
            Ingredient("Сыр Чеддер", 0, 9f, Category.CHEESE),
            Ingredient("Сыр Гауда", 0, 9f, Category.CHEESE),
            Ingredient("Сыр Пармезан", 0, 9f, Category.CHEESE),

            Ingredient("Ананас", 0, 7f, Category.EXTRA),
            Ingredient("Зелень", 0, 10f, Category.EXTRA),
        )
    }
}
