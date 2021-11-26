package com.example.ca1.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ca1.NEW_COCKTAIL_ID
import java.util.*

// we could choose not to provide a tableName, in which case
// the table would just be called 'CocktailEntity'
@Entity(tableName = "cocktails")
data class CocktailEntity (
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var text: String

) {
    // using this constructor when we don't yet know the ID, it's calling the primary constructor
    constructor() : this(NEW_COCKTAIL_ID, "")
    constructor(text: String) : this(NEW_COCKTAIL_ID, text)
}