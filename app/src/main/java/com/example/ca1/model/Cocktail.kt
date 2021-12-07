package com.example.ca1.model
// note: this file is equivalent to Rahul Pandey's 'post' file
//  here we want to define the attributes on the post
// the name, the type, and whether it's nullable

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

data class Cocktail(val idDrink: Int, val strDrink: String, val strInstructions: String, val strDrinkThumb: String, var ingredients: Map<String, String>)
    : Parcelable {
        constructor(parcel: Parcel): this(
            parcel.readInt()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readMap()
        )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(idDrink)
        parcel?.writeString(strDrink)
        parcel?.writeString(strInstructions)
        parcel?.writeString(strDrinkThumb)
        parcel?.writeBundle(ingredients)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cocktail> {
        override fun createFromParcel(parcel: Parcel): Cocktail {
            return Cocktail(parcel)
        }

        override fun newArray(size: Int): Array<Cocktail?> {
            return arrayOfNulls(size)
        }
    }
}