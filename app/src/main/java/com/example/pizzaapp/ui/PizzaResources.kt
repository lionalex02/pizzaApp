package com.example.pizzaapp.ui

import com.example.pizzaapp.R

object PizzaResources {

    fun getIngredientImage(name: String): Int {
        return when (name) {
            // Тесто
            "Neapolitan dough" -> R.drawable.img_dough_neapolitan
            //"Wheat dough" -> R.drawable.img_dough_wheat TODO:абоба

            // Соусы
            "Tomato Sauce" -> R.drawable.img_sauce_tomato
            "BBQ Sauce" -> R.drawable.img_sauce_bbq
            "White Saucce" -> R.drawable.img_sauce_white

            // Сыр
            //"Mozzarella" -> R.drawable.img_cheese_mozzarella TODO:абоба
            //"Parmesan" -> R.drawable.img_cheese_parmesan TODO:абоба
            "Base Cheese" -> R.drawable.img_cheese_base

            // Мясо
            "Pepperoni" -> R.drawable.img_meat_pepperoni
            "Bacon" -> R.drawable.img_meat_bacon
            //"Ham" -> R.drawable.img_meat_ham TODO:абоба

            // Овощи
            "Mushrooms" -> R.drawable.img_veggies_mushrooms
           // "Tomatoes" -> R.drawable.img_veggies_tomatoes TODO:абоба
            //"Pineapple" -> R.drawable.img_extra_pineapple TODO:абоба

            // Заглушка
            else -> R.drawable.ic_launcher_foreground
        }
    }
}