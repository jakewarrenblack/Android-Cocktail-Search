package com.example.ca1.model

// Our api is returning an object nested inside an array,
// so it's necessary to provide this wrapper for our Ingredient class
data class IngredientResponse (
    val ingredient: List<Ingredient>
)