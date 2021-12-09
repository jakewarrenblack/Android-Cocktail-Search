package com.example.ca1

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.CocktailEntity
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.data.MergedData
import com.example.ca1.data.SampleDataProvider
import com.example.ca1.model.Cocktail
import com.example.ca1.model.CocktailResponse
import com.example.plantapp.localDB.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

// https://www.geeksforgeeks.org/viewmodel-in-android-architecture-components/
// As part of the MVVM (Model, View, ViewModel) android architecture,
// we use the ViewModel class to store and manage UI-related data
// all the UI data is stored in the ViewModel rather than in the activity
// **separation of concerns**
class MainViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val json: MutableLiveData<String>
        get() = _json

    val _json: MutableLiveData<String> = MutableLiveData()


    // We do the same thing for each of these variables
    // We don't directly access these variables, we don't want to touch the livedata,
    // instead we have a getter method to provide access
    val _favourites: MutableLiveData<MutableList<FavouriteEntity?>?> = MutableLiveData()
    var tempFavourites = _favourites.value


    val favourites: LiveData<MutableList<FavouriteEntity?>?>
        get() = _favourites

    val _currentFavourite: MutableLiveData<FavouriteEntity> = MutableLiveData()

    val currentFavourite: LiveData<FavouriteEntity>
        get() = _currentFavourite


    // we don't expose live data directly to the activity in the MVVM model
    // we use MutableLiveData as a wrapper for our live data
    // this implies we may want to edit the MutableLiveData from the activity,
    // it can be changed at runtime
    // the underscore represents a private variable, it is only accessible to this MainViewModel.kt
    private val _cocktails: MutableLiveData<List<Cocktail>> = MutableLiveData()
    val cocktails: LiveData<List<Cocktail>>
        get() = _cocktails

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading



    // Retrieving a list of all of our cocktails based on the provided searchQuery
    fun getCocktails(searchQuery: String){
        // a coroutine function can only be called from a coroutine,
        // so we make one:
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
            // Retrieve all favourites so we know which cocktails need to display a 'solid' heart
            val fetchedCocktails = RetrofitInstance.api.getCocktails(searchQuery).drinks
            _cocktails.postValue(fetchedCocktails)

                val favourite =
                    database?.favouriteDao()?.getAll()

                _favourites.postValue(favourite)

                _isLoading.postValue(false)
            }
        }
    }

    // Accessing our local database, inserting the passed favourite object
    fun saveFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database?.favouriteDao()?.insertFavourite(favouriteEntity)

                _currentFavourite.postValue(favouriteEntity)
                tempFavourites?.add(favouriteEntity)
                _favourites.postValue(tempFavourites)
            }
        }
    }

    fun removeFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // Pass only an ID for this one, we're removing, not inserting an entity
                database?.favouriteDao()?.removeFavourite(favouriteEntity.id)
                _currentFavourite.postValue(null)
                tempFavourites?.remove(favouriteEntity)
                _favourites.postValue(tempFavourites)
            }
        }
    }

    fun getFavourite(favouriteId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favourite =
                    database?.favouriteDao()?.getFavouriteById(favouriteId)

                favourite?.let {
                    _currentFavourite.postValue(it)
                    Log.i("Favourite", "Cocktail Returned from DB" + it.strDrink)
                    //exists = true;
                }
            }
        }
    }

    fun getAllFavourites(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favourites =
                    database?.favouriteDao()?.getAll()

                favourites?.let {
                    _favourites.postValue(it)
                    //exists = true;
                }
            }
        }
    }


    // Here's our implementation of our MediatorLiveData
    // I'm using the mediator to allow accessing two data streams at once

    // the view seemed to have difficulty observing two different data streams,
    // so the mediator allows us to combine them into one and observe them at the same time
    fun fetchData(): MediatorLiveData<MergedData> {
        val liveDataMerger = MediatorLiveData<MergedData>()
        // we've already defined our sealed MergedData class, now we add our sources to it
        liveDataMerger.addSource(cocktails) {
            if (it != null) {
                liveDataMerger.value = MergedData.CocktailData(it)
            }
        }
        liveDataMerger.addSource(favourites) {
            if (it != null) {
                liveDataMerger.value = MergedData.FavouriteData(it)
            }
        }
        return liveDataMerger
    }

    // Here's our implementation of the raw json parsing
    // I'm calling this method because of the poor API structure
    // I would rather parse it manually than have almost 15 null values in my Cocktail class
    fun getFullJson(searchQuery: Int?){
        viewModelScope.launch {
            RetrofitInstance.api.getCocktailsJson(searchQuery).enqueue(object:
                Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // No need to handle error here, I'll do it in the view
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    //your raw string response
                    val stringResponse = response.body()?.string()
                    _json.postValue(stringResponse)
                }

            })
        }
    }
}