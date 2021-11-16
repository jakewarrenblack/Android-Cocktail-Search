package com.example.ca1

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ca1.data.SampleDataProvider.Companion.getCocktails
import com.example.ca1.model.Cocktail
import android.widget.SearchView

class SearchViewModel : ViewModel() {
        // I think I already have what I need to make this work...
        // Need to make the search page do most of what the mainviewmodel is currently doing
        // Have the mainviewmodel just display them
        // I may need to pass args in the nav_graph like I do from main > detail pages?
        // Except now I'm passing args to the api's search function instead


        private val _cocktails: MutableLiveData<List<Cocktail>> = MutableLiveData()

        val cocktails: LiveData<List<Cocktail>>
        get() = _cocktails

        private val _isLoading = MutableLiveData(false)
        val isLoading: LiveData<Boolean>
        get() = _isLoading


        init {
            getCocktails()
        }


        fun onQueryTextSubmit(p0: String?): Boolean {
            //Performs search when user hit the search button on the keyboard
//           if (bestCities.contains(p0)) {
//                   adapter.filter.filter(p0)
//                }
//           else {
//                  Toast.makeText(this@MainActivity, "No match found", Toast.LENGTH_SHORT).show()
//            }
            if (p0 != null) {
                Log.i("Search text",p0)
            }
            return false
        }

//        fun onQueryTextChange(p0: String?): Boolean {
//            //Start filtering the list as user start entering the characters
//            adapter.filter.filter(p0)
//            return false
//        }

}