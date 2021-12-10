package com.example.ca1

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var dao: CocktailDao
    private lateinit var database: AppDatabase

    // Run these functions before the tests
    @Before
    fun createDb(){
        // provide a context to get reference to DB
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        // initialise the DB
        // Create it as an in-memory Db, not stored in persistent storage during testing
        // pass appContext and Database class as params
        database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
        // allowMainThreadQueries means we can use the foreground thread during testing
        .allowMainThreadQueries()
        // build() initialises the Db
        .build()
        // double !! means it can't be null
        dao = database.cocktailDao()!!
    }

    // Run these functions after the tests
    @After
    fun closeDb(){
        database.close();
    }

    @Test
    fun createCocktails() {
        dao.insertAll(SampleDataProvider.getCocktails())
        val count = dao.getCount()
        // compare the count with the size of the data from the sample provider
        // should have same num of rows in Db as there are in the sample data provider
        assertEquals(count, SampleDataProvider.getCocktails().size)
    }
}