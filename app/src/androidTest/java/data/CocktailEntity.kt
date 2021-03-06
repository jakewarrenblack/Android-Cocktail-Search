package data

import com.example.ca1.NEW_COCKTAIL_ID
import java.util.*

data class CocktailEntity (
    var id:Int,
    var text: String

) {
    constructor() : this(NEW_COCKTAIL_ID, "")
    // using this constructor when we don't yet know the ID, it's calling the primary constructor
    constructor(text: String) : this(NEW_COCKTAIL_ID, text)
}