package com.example.ca1

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.CocktailEntity
import com.example.ca1.data.SampleDataProvider
import com.example.ca1.model.Cocktail
import kotlinx.coroutines.launch

// https://www.geeksforgeeks.org/viewmodel-in-android-architecture-components/
// As part of the MVVM (Model, View, ViewModel) android architecture,
// we use the ViewModel class to store and manage UI-related data
// all the UI data is stored in the ViewModel rather than in the activity
// **separation of concerns**
class MainViewModel : ViewModel() {

    //lateinit var searchQuery: String;



    var searchQuery: String = ""
        get() = field                     // getter
        set(value) { field = value }      // setter

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

    //val fragmentRef = MainFragment.searchQuery;


    init {
        // when the MainViewModel is initialised, set the value of our
        // MutableLiveData variable to the 'LiveData' received from the
        // SampleDataProvider. Right now this is mock data,
        // but in the future it will be coming from an API as real live data.

        //cocktailsList.value = SampleDataProvider.getCocktails()

        // this data will be shared with the UI
        getCocktails(searchQuery)
    }

    private fun getCocktails(searchQuery: String){
        // a coroutine function can only be called from a coroutine,
        // so we make one:
        viewModelScope.launch {
            _isLoading.value = true
            val fetchedCocktails = RetrofitInstance.api.getCocktails(searchQuery).drinks
            Log.i(TAG, "Fetched cocktails: $fetchedCocktails")
            _cocktails.value = fetchedCocktails
            _isLoading.value = false
        }
    }
}