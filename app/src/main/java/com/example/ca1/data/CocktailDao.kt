package com.example.ca1.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// DAO = Data Access Object
// Each database object gets its own function, marked with a room annotation
@Dao
interface CocktailDao{
    // this function can either insert new, or update existing where PK is the same
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCocktail(cocktail: CocktailEntity)

    // insert multiple cocktail objects
    // if a cocktail already exists in the DB, ignore the new ones
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(cocktails: List<CocktailEntity>)

    // this creates an observer to observe the Db
    // and automatically update the UI on change

    // ** cocktails won't actually have dates, I will change this in the future ** //
    @Query("SELECT * FROM cocktails ORDER BY id ASC")
    fun getAll(): LiveData<CocktailEntity>

    // we declare id as a param with a colon, like in PHP
    @Query("SELECT * FROM cocktails WHERE id = :id")
    // it will return a CocktailEntity object, which may be null - hence the question mark
    fun getCocktailById(id: Int):CocktailEntity?

    // See how many cocktails there are
    @Query("SELECT COUNT(*) FROM cocktails")
    fun getCount(): Int
}