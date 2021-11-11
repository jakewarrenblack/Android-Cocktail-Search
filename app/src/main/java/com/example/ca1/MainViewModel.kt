package com.example.ca1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ca1.data.CocktailEntity
import com.example.ca1.data.SampleDataProvider
// https://www.geeksforgeeks.org/viewmodel-in-android-architecture-components/
// As part of the MVVM (Model, View, ViewModel) android architecture,
// we use the ViewModel class to store and manage UI-related data
// all the UI data is stored in the ViewModel rather than in the activity
// **separation of concerns**
class MainViewModel : ViewModel() {
    // we don't expose live data directly to the activity in the MVVM model
    // we use MutableLiveData as a wrapper for our live data
    // this implies we may want to edit the MutableLiveData from the activity
    val cocktailsList = MutableLiveData<List<CocktailEntity>>()

    init {
        // when the MainViewModel is initialised, set the value of our
        // MutableLiveData variable to the 'LiveData' received from the
        // SampleDataProvider. Right now this is mock data,
        // but in the future it will be coming from an API as real live data.
        cocktailsList.value = SampleDataProvider.getCocktails()
    }
}