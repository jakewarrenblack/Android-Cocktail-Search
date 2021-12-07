package com.example.ca1.model


import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

data class Ingredient(val idIngredient: String?, val strIngredient: String, val strDescription: String, ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(idIngredient)
        parcel?.writeString(strIngredient)
        parcel?.writeString(strDescription)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }
}