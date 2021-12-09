package com.example.ca1.model
// note: this file is equivalent to Rahul Pandey's 'post' file
//  here we want to define the attributes on the post
// the name, the type, and whether it's nullable

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.bundleOf
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cocktail(val idDrink: Int,
                    val strDrink: String,
                    val strInstructions: String,
                    val strDrinkThumb: String,
                    // Transient tells retrofit to ignore this field, I don't want retrofit to try and fill this in
                    // We're doing that separately with the raw json parsing
                    @Transient
                    var ingredients: MutableMap<String, String>? = null
                )
    : Parcelable