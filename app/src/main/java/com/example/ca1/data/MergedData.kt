package com.example.ca1.data

import com.example.ca1.model.Cocktail

sealed class MergedData {
    data class CocktailData(val cocktailItems: List<Cocktail>): MergedData()
    data class FavouriteData(val favouriteItems: MutableList<FavouriteEntity?>?): MergedData()

    data class jsonData(val jsonItems: String): MergedData()
}