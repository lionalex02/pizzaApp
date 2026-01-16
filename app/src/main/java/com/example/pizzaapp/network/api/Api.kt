package com.example.pizzaapp.network.api

import com.example.pizzaapp.domain.model.FoodDetailsResponse
import com.example.pizzaapp.domain.model.FoodSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api{

//    @GET("search")
//    suspend fun getCalories(
//        @Query("q") ingredient : String
//    )

    @GET("foods/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 1,
        @Query("api_key") apiKey: String,
        @Query("dataType") dataType: List<String> = listOf("Foundation", "SR Legacy", "Survey (FNDDS)")
    ): FoodSearchResponse

    @GET("food/{fdcId}")
    suspend fun getFoodDetails(
        @Path("fdcId") id: Long,
        @Query("api_key") apiKey: String
    ): FoodDetailsResponse
}