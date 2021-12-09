package com.example.ca1

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.data.MergedData
import com.example.ca1.databinding.MainFragmentBinding
import com.example.ca1.model.Cocktail
import org.json.JSONArray
import org.json.JSONObject

class MainFragment : Fragment(),
    // Useful article explaining MVVM, Observers and Observables, and the difference between each layer of the MVVM architecture
    // https://dev.to/productivebot/understanding-the-flow-of-data-in-mvvm-architecture-487

    // implement the ListItemListener interface from CocktailsListAdapter
    // We've defined methods in our CocktailsListAdapter which are implemented in here
    // It's necessary to use interfaces because our adapter doesn't have direct access to the data in our viewmodel,
    // so our fragment is acting as a go-between
    CocktailsListAdapter.ListItemListener{
    // creating the viewModel for the MainActivity -
    // an activity (like this mainfragment.kt) must extend the ViewModel class in order to create a ViewModel
    private lateinit var viewModel: MainViewModel
    private lateinit var searchQuery: String
    // add reference to binding class
    // underscores replace with uppercase, word binding at the end
    // MainFragmentBinding is a generated class
    private lateinit var binding: MainFragmentBinding
    private lateinit var adapter: CocktailsListAdapter
    private val args: MainFragmentArgs by navArgs()
    private lateinit var spinner: ProgressBar
    var cocktailItems: List<Cocktail>? = null
    var favouriteItems: MutableList<FavouriteEntity?>? = null
    private lateinit var responseJson: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Getting our search query from the search page
        searchQuery = args.searchQuery;

        // make the back icon disappear when not on the single page
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Once again we get a reference to the binding, which allows us access to all the views within this fragment without using
        binding = MainFragmentBinding.inflate(inflater, container, false)
        spinner = binding.progressBar1

        // It's important to obtain an instance of the viewModel during view creation
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val liveData = viewModel.fetchData()
        viewModel.getCocktails(searchQuery)

        // this codeblock allows us to reference the recyclerView binding many times
        // getting a hold of the recyclerView defined in the main_fragment.xml layout file
        // we then set some styling for it
        with(binding.mainRecyclerView){
            // height of each row same regardless of contents
            setHasFixedSize(true)
            // this is from the recyclerView's widget package
            val divider = DividerItemDecoration(
                    context, LinearLayoutManager(context).orientation
            )
        }

        // So, in the MVVM architecture,
        // the idea of the 'Observer pattern'
        // is to facilitate the flow of data between our components (the model, the view-model, and the view),
        // while keeping these layers separate

        // Below is our Observer, which is observing some values in our viewmodel

        // the MainViewModel.kt is our view-model (the MutableLiveData is our **observable**)

        // The view-model, in this way, facilitates the communications between the model and the views
        // Similarly, Views don't communicate directly with each other.
        // Instead, the data is passed through the view models.

        // Mine is a bit different from the normal pattern,
        // In that I'm using the 'MediatorLiveData' to observe multiple data streams in the one observer

        // I'm doing this because the view seemed to have trouble observing two streams using separate observers,
        // and I need them to arrive in parallel
        liveData.observe(viewLifecycleOwner,
            { it ->

                // here we say 'when the return value of the observer = CocktailData, Favourite data, assign these values to our cocktailItems and favouriteItems lists in here
                when(it){
                    is MergedData.CocktailData -> cocktailItems = it.cocktailItems
                    is MergedData.FavouriteData -> favouriteItems = it.favouriteItems
                }

                if(cocktailItems?.isNotEmpty() == true) {
                    if (cocktailItems != null && favouriteItems != null) {

                        // Now we have our data, and we can pass it in to our adapter
                        // the adapter is tied to our recycler view below, and we'll use it to set the values inside each list item
                        adapter =
                            CocktailsListAdapter(cocktailItems, favouriteItems, this@MainFragment)
                        binding.mainRecyclerView.adapter = adapter
                        binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity)
                    }
                }else{
                    // If we don't retrieve any cocktails (no search results), display a message to let the user know there are no results for their query
                    binding.noCocktailsFound.visibility = View.VISIBLE
                }

                // Showing or hiding the loading indicator based on whether or not we've retrieved our data yet
                if(it == null){
                    spinner.visibility = View.VISIBLE;
                } else{
                    spinner.visibility = View.GONE;
                }
            })
        return binding.root
    }

    // We've defined interfaces for the onItemClick and onSaveClick methods in our CocktailListAdapter, and now we implement them here
    override fun onItemClick(cocktailId: Int, cocktailName: String, cocktailInstructions: String, cocktailImage: String, fragmentName: String) {
        Log.i(TAG, "onItemClick: received cocktail id $cocktailId")
        // sending data from MainFragment to ViewFragment
        val action = MainFragmentDirections.actionViewCocktail(cocktailId, cocktailName, cocktailInstructions, cocktailImage, fragmentName)
        // get a reference to the navigation host, passing in a strongly typed value (an int)
        // means we don't have to interpret the passed data on the other side,
        //  there's no risk of us messing it up because it's now strongly typed
        findNavController().navigate(action)
    }

    override fun onSaveClick(cocktail: Cocktail, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int) {
        // every time you click, run getFavourite on the cocktailId of this specific list item
        // viewModel.getFavourite then sets the value of currentFavourite
        if(favouriteItems?.contains(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb)) == true){
            Log.i("FavouriteExistence", "Cocktail already exists, unsaving : ${cocktail.idDrink} / adapterfavourite: $adapterFavouriteId")
            favouriteItems?.remove(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            viewModel.removeFavourite(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            adapter = CocktailsListAdapter(cocktailItems,favouriteItems, this@MainFragment)

            //adapter.notifyItemChanged(position);
            //adapter.notifyDataSetChanged()
        }
        else{
            Log.i("FavouriteExistence", "Cocktail does not already exist, saving: ${cocktail.idDrink} / adapterfavourite: $adapterFavouriteId")
            // If this cocktailId does not already correspond with an existing favourite
            favouriteItems?.add(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            viewModel.saveFavourite(FavouriteEntity(cocktail.idDrink, cocktail.strDrink, cocktail.strInstructions, cocktail.strDrinkThumb))
            adapter = CocktailsListAdapter(cocktailItems,favouriteItems, this@MainFragment)

        }
    }
}