package com.example.ca1

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.model.Cocktail
import com.example.ca1.model.Ingredient
import com.example.plantapp.localDB.AppDatabase
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

    val json: MutableLiveData<String>
    get() = _json

    private val _ingredientDetails: MutableLiveData<List<Ingredient>> = MutableLiveData()
    val ingredientDetails: LiveData<List<Ingredient>>
        get() = _ingredientDetails

    val _json: MutableLiveData<String> = MutableLiveData()

    fun getIngredientDetailsById(id: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fetchedIngredientDetails = RetrofitInstance.api.getIngredientById(id).ingredient
                Log.i(TAG, "Fetched ingredient details: $fetchedIngredientDetails")
                _ingredientDetails.postValue(fetchedIngredientDetails)
            }
        }
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

    fun getFullJson(searchQuery: Int?){
        viewModelScope.launch {
            RetrofitInstance.api.getCocktailsJson(searchQuery).enqueue(object:
                Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
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