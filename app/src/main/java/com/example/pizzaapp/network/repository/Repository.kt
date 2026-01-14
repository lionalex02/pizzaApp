package com.example.pizzaapp.network.repository

import com.example.pizzaapp.BuildConfig
import com.example.pizzaapp.domain.model.Category
import com.example.pizzaapp.domain.model.Ingredient
import com.example.pizzaapp.network.api.Api
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow

class Repository(
    private val api: Api
) {
    val apiKey = BuildConfig.API_KEY
    suspend fun getCalories(name: String) :Int {
        val id = api.searchFood(name, 1, apiKey)
            .foods.first().fdcId
        val calories = api.getFoodDetails(id, apiKey).foodNutrients.firstOrNull {
            it.nutrient.name == "Energy" && it.nutrient.unitName == "kcal"
        }?.amount?.toInt() ?: 0
        return calories

    }

     fun getIngredients(): Flow<List<Ingredient>> {
        return flow {
            emit(loadIngredients())
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

            Ingredient("Кетчуп", ketchupCal.await(), 1f, Category.SAUCE),
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
}
