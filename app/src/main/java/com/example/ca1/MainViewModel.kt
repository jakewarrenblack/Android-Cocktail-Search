package com.example.ca1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ca1.data.CocktailEntity
import com.example.ca1.data.SampleDataProvider

class MainViewModel : ViewModel() {

    val cocktailsList = MutableLiveData<List<CocktailEntity>>()

    init {
        cocktailsList.value = SampleDataProvider.getCocktails()
    }
}