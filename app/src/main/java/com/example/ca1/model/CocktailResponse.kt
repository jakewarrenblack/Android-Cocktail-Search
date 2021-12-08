package com.example.ca1.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CocktailResponse (val drinks: List<Cocktail>, val ingredients: List<Ingredient>?) :
    Parcelable