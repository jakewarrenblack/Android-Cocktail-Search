package com.example.ca1.model


import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ingredient(val idIngredient: String?, val strIngredient: String?, val strDescription: String?, ): Parcelable
