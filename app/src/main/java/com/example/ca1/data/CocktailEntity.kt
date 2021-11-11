package com.example.ca1.data

import com.example.ca1.NEW_COCKTAIL_ID
import java.util.*

data class CocktailEntity (
    var id:Int,
    var date: Date,
    var text: String

) {
    // using this constructor when we don't yet know the ID, it's calling the primary constructor
    constructor() : this(NEW_COCKTAIL_ID, Date(), "")
    constructor(date: Date, text: String) : this(NEW_COCKTAIL_ID, date, text)
}