package com.example.ca1

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.ca1.data.FavouriteEntity
import com.example.plantapp.localDB.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val _currentFavourite: MutableLiveData<FavouriteEntity> = MutableLiveData()

    val currentFavourite: LiveData<FavouriteEntity>
    get() = _currentFavourite

    fun getFavourite(favouriteId: Int) {
        Log.i("Favourite check for", "Id : " + favouriteId)

        // ** Was using this, the return type of this method used to be a boolean, not necessary **
        // Will return true or false from this method,
        // false by default, but if the passed cocktailId is an existing favourite, returns true
        //var exists: Boolean = false;

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
//        if(exists){
//            return true;
//        }
//        return false;
    }

    fun saveFavourite(favouriteEntity: FavouriteEntity) {

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database?.favouriteDao()?.insertFavourite(favouriteEntity)

                // Not sure what I'm doing here,
                // Trying to get the UI to update when a save is made
                getFavourite(favouriteEntity.id);
            }
        }
    }

    fun removeFavourite(favouriteEntity: FavouriteEntity) {

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // Pass only an ID for this one, we're removing, not inserting an entity
                database?.favouriteDao()?.removeFavourite(favouriteEntity.id)

                _currentFavourite.postValue(null)
                    //exists = true;
            }
        }
    }
}