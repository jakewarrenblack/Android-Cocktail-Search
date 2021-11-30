package com.example.ca1.model
// note: this file is equivalent to Rahul Pandey's 'post' file
//  here we want to define the attributes on the post
// the name, the type, and whether it's nullable

import android.os.Parcel
import android.os.Parcelable

data class Cocktail(
    val idDrink: Int,
    val strDrink: String,
    val strInstructions: String,
    val strDrinkThumb: String,
    var ingredients: MutableMap<String, String>
)