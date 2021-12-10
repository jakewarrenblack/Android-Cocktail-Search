package com.example.ca1.data

import com.example.ca1.model.Cocktail
import com.example.ca1.model.Ingredient

sealed class MergedData {
    data class CocktailData(val cocktailItems: List<Cocktail>): MergedData()
    data class FavouriteData(val favouriteItems: MutableList<FavouriteEntity?>?): MergedData()
    data class CurrentFavouriteData(val currentFavouriteItem: FavouriteEntity?): MergedData()
    data class IngredientsData(val ingredientItems: MutableList<Ingredient?>): MergedData()

    data class jsonData(val jsonItems: String): MergedData()
}