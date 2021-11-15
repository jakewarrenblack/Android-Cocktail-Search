package com.example.ca1.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// we are going to directly access the fields we define in here,
// which is why this is an object, instead of a class

// we can't use a '?' in our base_url, I will append this in the API interface
private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    // the rest of our app will use this 'api' var to make calls to the network api
    val api: CocktailApi by lazy{
        retrofit.create(CocktailApi::class.java)

    }

}
//TODO: fix
// this is getting messed up on me,
// throws error 'com.squareup.moshi.JsonDataException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at path $'
// the issue (I am guessing)
// is that retrofit is making a straight-up call to the base_url + the thing we append in the cocktailapi file
// but I need it to get response.data.drinks
// the plain response is an object, and inside it is the array we want
// not sure how to fix this
// maybe https://stackoverflow.com/questions/52147481/how-to-parse-array-inside-object-with-retrofit-android