package com.example.pizzaapp.ui

import com.example.pizzaapp.R

object PizzaResources {

    fun getIngredientImage(name: String): Int {
        return when (name) {
            // Тесто
            "Неаполитанское тесто" -> R.drawable.img_dough_neapolitan
            //"Пшеничное тесто" -> R.drawable.img_dough_wheat TODO:абоба
            //"Ржаное тесто" -> R.drawable.img_dough_wheat TODO:абоба

            // Соусы
            "Томатный соус" -> R.drawable.img_sauce_tomato
            "Соус Барбекю" -> R.drawable.img_sauce_bbq
            "Майонез" -> R.drawable.img_sauce_white

            // Сыр
            //"Сыр Моцарелла" -> R.drawable.img_cheese_mozzarella TODO:абоба
            //"Сыр Чеддер" -> R.drawable.img_cheese_parmesan TODO:
            //"Сыр Гауда" -> R.drawable.img_cheese_parmesan TODO:абоба
            //"Сыр Пармезан" -> R.drawable.img_cheese_parmesan TODO:абоба
            "Base Cheese" -> R.drawable.img_cheese_base

            // Мясо
            "Колбаса пепперони" -> R.drawable.img_meat_pepperoni
            "Бекон" -> R.drawable.img_meat_bacon
            //"Куриное филе" -> R.drawable.img_meat_ham TODO:абоба
            //"Салями" -> R.drawable.img_meat_ham TODO:абоба
            //"Ветчина" -> R.drawable.img_meat_ham TODO:абоба

            // Овощи
            "Шампиньоны" -> R.drawable.img_veggies_mushrooms
           // "Томаты" -> R.drawable.img_veggies_tomatoes TODO:абоба
            //"Ананас" -> R.drawable.img_extra_pineapple TODO:абоба
            //"Зелень" -> R.drawable.img_extra_pineapple TODO:абоба
            //"Оливки" -> R.drawable.img_extra_pineapple TODO:абоба
            //"Халапеньо" -> R.drawable.img_extra_pineapple TODO:абоба
            //"Маринованные огурцы" -> R.drawable.img_extra_pineapple TODO:абоба

            // Заглушка
            else -> R.drawable.ic_launcher_foreground
        }
    }
}