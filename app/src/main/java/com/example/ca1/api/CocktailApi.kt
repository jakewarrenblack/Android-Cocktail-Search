package com.example.ca1.api

import com.example.ca1.data.CocktailEntity
import com.example.ca1.model.Cocktail
import com.example.ca1.model.CocktailResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// retrofit will implement this interface
interface CocktailApi {
    // the suspend keyword indicates that this
    // function should be called from a coroutine
    // retrofit has built-in support for coroutines
    //@GET("search.php?s={searchQuery}")
    //suspend fun getCocktails(@Query("searchQuery") searchQuery: String?): CocktailResponse
//    suspend fun getCocktails(@Path("searchQuery") String searchQuery): CocktailResponse

    // I was doing it like this, throwing HTTP 403
    @GET("search.php")
    suspend fun getCocktails(@Query("s") searchQuery: String): CocktailResponse

    // I want to get the raw json response for this one, I'll be looping through the ingredient and measure data
    @GET("search.php")
    fun getCocktailsJson(@Query("s") searchQuery: String): Call<ResponseBody>

    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") searchQuery: Int?): CocktailResponse

}