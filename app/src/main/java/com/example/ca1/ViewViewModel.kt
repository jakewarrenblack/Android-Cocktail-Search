package com.example.ca1

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.data.MergedData
import com.example.ca1.model.Cocktail
import com.example.ca1.model.Ingredient
import com.example.plantapp.localDB.AppDatabase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewViewModel (app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    val _currentFavourite: MutableLiveData<FavouriteEntity> = MutableLiveData()
    val currentFavourite: LiveData<FavouriteEntity>
    get() = _currentFavourite

    // This can be null, not all ingredients will have details
    private val _ingredientDetails: MutableLiveData<List<Ingredient>> = MutableLiveData()
    val ingredientDetails: LiveData<List<Ingredient>>
        get() = _ingredientDetails

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _ingredientDetailsList: MutableLiveData<List<Ingredient>> = MutableLiveData()
    val ingredientDetailsList: LiveData<List<Ingredient>>
        get() = _ingredientDetailsList


    fun getIngredientDetailsByName(name: String){
        viewModelScope.launch {
            val fetchedIngredientDetails = RetrofitInstance.api.getIngredientByName(name).ingredients
            Log.i(TAG, "Fetched ingredient details A: ${fetchedIngredientDetails}")
            _ingredientDetails.postValue(fetchedIngredientDetails)

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