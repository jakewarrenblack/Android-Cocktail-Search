package com.example.ca1.api

import com.example.ca1.data.CocktailEntity
import retrofit2.http.GET

// retrofit will implement this interface
interface CocktailApi {
    // the suspend keyword indicates that this
    // function should be called from a coroutine
    // retrofit has built-in support for coroutines
    @GET("search.php?s=margarita")
    suspend fun getCocktails(): List<CocktailEntity>

}