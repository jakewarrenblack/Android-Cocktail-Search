package com.example.ca1.model
// note: this file is equivalent to Rahul Pandey's 'post' file
//  here we want to define the attributes on the post
// the name, the type, and whether it's nullable

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.bundleOf

data class Cocktail(val idDrink: Int,
                    val strDrink: String,
                    val strInstructions: String,
                    val strDrinkThumb: String,
                    @Transient
                    var ingredients: MutableMap<String, String>? = null
                )
    : Parcelable {
        constructor(parcel: Parcel): this(
            parcel.readInt()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
        )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(idDrink)
        parcel?.writeString(strDrink)
        parcel?.writeString(strInstructions)
        parcel?.writeString(strDrinkThumb)
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