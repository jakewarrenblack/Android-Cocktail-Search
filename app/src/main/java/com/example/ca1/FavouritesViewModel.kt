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
import com.example.plantapp.localDB.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouritesViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val _favourites: MutableLiveData<MutableList<FavouriteEntity?>?> = MutableLiveData()

    val favourites: LiveData<MutableList<FavouriteEntity?>?>
        get() = _favourites

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _cocktails: MutableLiveData<List<Cocktail>> = MutableLiveData()
    val cocktails: LiveData<List<Cocktail>>
        get() = _cocktails

    init{
        getFavourites()
    }


    fun getFavourites(){
        viewModelScope.launch {
            //_isLoading.postValue(true)
            withContext(Dispatchers.IO) {
                val favourite =
                    database?.favouriteDao()?.getAll()

                favourite?.let {
                    _favourites.postValue(it)
                }
                //_isLoading.postValue(false)
            }
        }
    }
    fun getCocktails(favourites: MutableList<FavouriteEntity?>?){
        viewModelScope.launch {
            _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
        val iterator = favourites?.listIterator()
                //if (iterator != null) {
                    //for (item in iterator) {
                        _isLoading.postValue(true)
                        val fetchedCocktails = RetrofitInstance.api.getCocktailById(favourites?.get(0)?.id).drinks
                        Log.i(TAG, "Fetched cocktails: $fetchedCocktails")
                        _cocktails.postValue(fetchedCocktails)
                        _isLoading.postValue(false)
                    //}
               // }
            }
        }
    }
}