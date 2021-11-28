package com.example.plantapp.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.localDB.FavouriteDao

// If you make changes to your Entity class and therefore need to make changes to your table easiest thing
// to do is change the version here and uncomment .fallbackToDestructiveMigration() below.
// This will scrap the whole database and start again.
@Database(entities = [FavouriteEntity::class], version = 3, exportSchema = false)

abstract class AppDatabase: RoomDatabase() {

    // this will be instantiated and all the abstract methods in the DAO will be implemented
    abstract fun favouriteDao(): FavouriteDao?

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "favourites.db"
                    )//.fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}