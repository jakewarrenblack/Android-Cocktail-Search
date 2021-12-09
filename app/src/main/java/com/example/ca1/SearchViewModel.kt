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
    // No implementation necessary here, our search query is handled in the SearchFragment and the MainFragment
}