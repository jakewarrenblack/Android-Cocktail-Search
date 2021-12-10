package com.example.ca1

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.data.MergedData
import com.example.ca1.model.Ingredient
import com.example.plantapp.localDB.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val _currentFavourite: MutableLiveData<FavouriteEntity> = MutableLiveData()
    val currentFavourite: LiveData<FavouriteEntity>
    get() = _currentFavourite

    // This can be null, not all ingredients will have details
    private val _ingredientDetails: MutableLiveData<MutableList<Ingredient?>> = MutableLiveData()
    val ingredientDetails: LiveData<MutableList<Ingredient?>>
        get() = _ingredientDetails

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    // I'm trying to make a temporary list like I do in favourites,
    // I want to add to this list every time, and then assign its' value
    // to the real list, the one we feed into the mediatorlivedata
    var tempIngredientDetails: MutableList<Ingredient?> = mutableListOf()


    fun getIngredientDetailsByName(name: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fetchedIngredientDetails =
                    RetrofitInstance.api.getIngredientByName(name).ingredients
                Log.i(TAG, "Fetched ingredient details A: ${fetchedIngredientDetails}")
                // Every time this runs, it replaces the entire value of _ingredientDetails with the entire value **received** from the api
                // It replaces one MutableList, with another MutableList,
                // I need to figure out how to make it *add* to the existing list

                // Get a single ingredientDetail back, and add it to the temporary list
                val ingredient: Ingredient? = fetchedIngredientDetails?.first()

                tempIngredientDetails.add(ingredient)

                // And then assign the value of the temp list to _ingredientDetails, giving us a full list
                _ingredientDetails.postValue(tempIngredientDetails)
            }

        }
    }

    fun fetchData(ingredients: MutableMap<String, String>): MediatorLiveData<MergedData> {
        val liveDataMerger = MediatorLiveData<MergedData>()
        var i: Int = 0;
        for ((key, value) in ingredients.entries) {
            i++
            if(key.isNotEmpty() || value.isNotEmpty()) {
                getIngredientDetailsByName(value)
            }
        }
        // At this point it breaks, we if we debug we can see that ingredientDetails has been populated with exactly what we want,
        // but the liveDataMerger still only gets 1 value
        liveDataMerger.addSource(ingredientDetails) {
            if (it != null && it.size == i) {
                liveDataMerger.value = MergedData.IngredientsData(it)
            }
        }

        liveDataMerger.addSource(currentFavourite) {
                liveDataMerger.value = MergedData.CurrentFavouriteData(it)
        }
        return liveDataMerger
    }



    fun getFavourite(favouriteId: Int) {
        Log.i("Favourite check for", "Id : " + favouriteId)
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
}