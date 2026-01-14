package com.example.pizzaapp.di

import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.pizzaapp.network.api.Api
import com.example.pizzaapp.network.repository.Repository
import com.example.pizzaapp.network.api.Provider
import com.example.pizzaapp.prezentation.PizzaViewModel
import org.koin.dsl.module


val appModule = module {
    single<Api> {
        Provider.api
    }

    single{
        Repository(get())
    }

    viewModel {
        PizzaViewModel(get())
    }


}
