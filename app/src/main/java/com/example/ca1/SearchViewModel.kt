package com.example.ca1

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ca1.data.SampleDataProvider.Companion.getCocktails
import com.example.ca1.model.Cocktail
import android.widget.SearchView
import androidx.lifecycle.viewModelScope
import com.example.ca1.api.RetrofitInstance
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    // I think I already have what I need to make this work...
    // Need to make the search page do most of what the mainviewmodel is currently doing
    // Have the mainviewmodel just display them
    // I may need to pass args in the nav_graph like I do from main > detail pages?
    // Except now I'm passing args to the api's search function instead

    // get a reference to the main fragment's viewmodel
    // if i understand the architecture right...
    // i don't think i should pass data straight from the search fragment to the mainviewmodel
    // so i instead am going to call a function in this searchviewmodel from the search fragment,
    // pass data from here to the mainviewmodel,
    // and then hopefully the observer in the main fragment will pick up on the passed data
    // the intention is to be able to search for a cocktail and have the api actually search for your search term,
    // ------------------------------------------------------- //



    fun getCocktails(searchQuery: String){
        // a coroutine function can only be called from a coroutine,
        // so we make one:
        viewModelScope.launch {
            val fetchedCocktails = RetrofitInstance.api.getCocktails(searchQuery).drinks
            Log.i(TAG, "Fetched cocktails: $fetchedCocktails")

        }
    }

}