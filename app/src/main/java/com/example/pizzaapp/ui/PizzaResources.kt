package com.example.pizzaapp.ui

import com.example.pizzaapp.R

object PizzaResources {

    fun getIngredientImage(name: String): Int {
        return when (name) {
            // Тесто
            "Неаполитанское тесто" -> R.drawable.img_dough_neapolitan
            "Пшеничное тесто" -> R.drawable.img_dough_wheat
            "Ржаное тесто" -> R.drawable.img_dough_rye

            // Соусы
            "Томатный соус" -> R.drawable.img_sauce_tomato
            "Соус Барбекю" -> R.drawable.img_sauce_bbq
            "Майонез" -> R.drawable.img_sauce_white

            // Сыр
            "Сыр Моцарелла" -> R.drawable.img_cheese_mozzarella
            "Сыр Чеддер" -> R.drawable.img_cheese_base
            "Сыр Гауда" -> R.drawable.img_cheese_gouda
            "Сыр Пармезан" -> R.drawable.img_cheese_parmesan


            // Мясо
            "Колбаса пепперони" -> R.drawable.img_meat_pepperoni
            "Бекон" -> R.drawable.img_meat_bacon
            "Куриное филе" -> R.drawable.img_meat_chicken
            "Котенок" -> R.drawable.img_meat_kitty
            "Ветчина" -> R.drawable.img_meat_ham

            // Овощи
            "Шампиньоны" -> R.drawable.img_veggies_mushrooms
            "Томаты" -> R.drawable.img_veggies_tomatoes
            "Ананас" -> R.drawable.img_extra_pineapple
            "Зелень" -> R.drawable.img_veggies_herbs
            "Оливки" -> R.drawable.img_veggies_olives
            "Халапеньо" -> R.drawable.img_veggies_jalapeno
            "Маринованные огурцы" -> R.drawable.img_veggies_pickles

            // Заглушка
            else -> R.drawable.ic_launcher_foreground
        }
    }
}