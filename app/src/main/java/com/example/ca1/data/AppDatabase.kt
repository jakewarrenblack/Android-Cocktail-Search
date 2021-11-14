package com.example.ca1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// the room library will create the 'concrete' version of this abstract class at runtime
// the entities property defines which entities are part of the db structure, in our case only 1
// this is our first db, so version 1, and no point exporting schema documentation for us
@Database(entities = [CocktailEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    // for each DAO, create an abstract function
    abstract fun cocktailDao(): CocktailDao?

    companion object{
        // instantiate the class
        private var INSTANCE: AppDatabase? = null

        // specify that this function will return an instance of this AppDatabase class
        fun getInstance(context: Context): AppDatabase?{
            if(INSTANCE == null){
                // ensure DB init only runs once at a time
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "CA1.db"
                    ).build()
                }
            }
            // if instance already exists, just return it anyway
            return INSTANCE
        }
    }
}