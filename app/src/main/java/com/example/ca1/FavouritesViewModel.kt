package com.example.ca1

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.model.Cocktail
import com.example.plantapp.localDB.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouritesViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val _favourites: MutableLiveData<MutableList<FavouriteEntity?>?> = MutableLiveData()

    val favourites: LiveData<MutableList<FavouriteEntity?>?>
        get() = _favourites

    var tempFavourites = _favourites.value

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _cocktails: MutableLiveData<List<Cocktail>> = MutableLiveData()
    val cocktails: LiveData<List<Cocktail>>
        get() = _cocktails

    init{
        getFavourites()
    }

    fun clearFavourites(){
        viewModelScope.launch{
            _favourites.postValue(null)
        }
    }


    // returning all saved favourites
    private fun getFavourites(){
        viewModelScope.launch {
            //_isLoading.postValue(true)
            withContext(Dispatchers.IO) {
                val favourite =
                    database?.favouriteDao()?.getAll()

                favourite?.let {
                    _favourites.postValue(it)
                }
            }
        }
    }

    // Removing a favourite object from our local db
    fun removeFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // Pass only an ID for this one, we're removing, not inserting an entity
                database?.favouriteDao()?.removeFavourite(favouriteEntity.id)
                //_currentFavourite.postValue(null)
                tempFavourites?.remove(favouriteEntity)
                _favourites.postValue(tempFavourites)
            }
        }
    }

    // Passing in our favourite objects and retrieving cocktails based on them,
    // as they have the same attributes
    fun getCocktails(favourites: MutableList<FavouriteEntity?>?){
        viewModelScope.launch {
            _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
        val iterator = favourites?.listIterator()
                _isLoading.postValue(true)
                if(favourites?.isNotEmpty() == true) {
                    // get the first value in the list
                    val fetchedCocktails =
                        RetrofitInstance.api.getCocktailById(favourites.get(0)?.id).drinks
                    _cocktails.postValue(fetchedCocktails)
                    _isLoading.postValue(false)
                }
                else{
                    _cocktails.postValue(null)
                }
            }
        }
    }
}