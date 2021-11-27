package com.example.ca1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
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

class MainFragment : Fragment(),
    // Useful article explaining MVVM, Observers and Observables, and the difference between each layer of the MVVM architecture
    // https://dev.to/productivebot/understanding-the-flow-of-data-in-mvvm-architecture-487

    // implement the ListItemListener interface from CocktailsListAdapter
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


    //private lateinit var searchViewModel: SearchViewModel





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Getting our search query from the search page
        searchQuery = args.searchQuery;



        if(searchQuery != "None"){
            Log.i("SEARCH PASSED", searchQuery)
        }

        // make the back icon disappear when not on the single page
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding = MainFragmentBinding.inflate(inflater, container, false)

        spinner = binding.progressBar1

        // It's important to obtain an instance of the viewModel during view creation
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val liveData = viewModel.fetchData()
        viewModel.getCocktails(searchQuery)


        var cocktailItems: List<Cocktail>? = null
        var favouriteItems: List<FavouriteEntity>? = null

        // trying to initialise the searchViewModel

        //searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        //searchViewModel.getCocktails(searchQuery)

//        viewModel.searchQuery = searchQuery;

        //viewModel.getCocktails(searchQuery)


        // now we have references to all child view components within the layout

        // this codeblock allows us to reference the recyclerView binding many times
        // getting a hold of the recyclerView defined in the main_fragment.xml layout file
        // we then set some styling for it
        with(binding.recyclerView){
            // height of each row same regardless of contents
            setHasFixedSize(true)
            // this is from the recyclerView's widget package
            val divider = DividerItemDecoration(
                    context, LinearLayoutManager(context).orientation
            )
            // this creates a visual divider between each row
            //addItemDecoration(divider)

        }

        // So, in the MVVM architecture,
        // the idea of the 'Observer pattern'
        // is to facilitate the flow of data between our components (the model, the view-model, and the view),
        // while keeping these layers separate

        // Below is our Observer, which usually is a view or a view-model
        // and our Observable class is defined in our view-model

        // so in our case:
        // this file is our view (our observer is below!!)
        // the MainViewModel.kt is our view-model (the MutableLiveData is our **observable**)
        // and our SampleDataProvider (will be an API in the future) is our model

        // The view-model, in this way, facilitates the communications between the model and the views
        // Similarly, Views don't communicate directly with each other.
        // Instead, the data is passed through the view models.

        // The cocktails in here could do with being moved back into the ViewViewModel I think?
//        searchViewModel.cocktails.observe(viewLifecycleOwner, Observer{
//            if(searchViewModel.cocktails.value == null){
//                spinner.visibility = View.VISIBLE;
//            }
//            else{
//                spinner.visibility = View.GONE;
//            }
//            Log.i("CocktailLogging:", it.toString())
//            adapter = CocktailsListAdapter(it, this@MainFragment)
//            binding.recyclerView.adapter = adapter
//            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
//        })
//
        liveData.observe(viewLifecycleOwner,
            { it ->
                if(it == null){
                    spinner.visibility = View.VISIBLE;
                } else{
                    spinner.visibility = View.GONE;
                }

                when(it){
                    is MergedData.CocktailData -> cocktailItems = it.cocktailItems
                    is MergedData.FavouriteData -> favouriteItems = it.favouriteItems
                }

                if (cocktailItems != null && favouriteItems != null) {
                    Log.i("CocktailLogging:", it.toString())
                    adapter = CocktailsListAdapter(cocktailItems,favouriteItems, this@MainFragment)
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(activity)
                    //                    liveData.removeObserver(this)
                    //                    liveData.removeObserver(this)
                }
            })



        return binding.root
    }

//    fun toggleSpinner(){
//        if(spinner.visibility == View.VISIBLE){
//            spinner.visibility = View.GONE;
//        }else{
//            spinner.visibility = View.VISIBLE;
//        }
//    }

    override fun onItemClick(cocktailId: Int, cocktailInstructions: String, cocktailName: String) {
        Log.i(TAG, "onItemClick: received cocktail id $cocktailId")
        // sending data from MainFragment to ViewFragment
        val action = MainFragmentDirections.actionViewCocktail(cocktailId, cocktailInstructions, cocktailName)
        // get a reference to the navigation host, passing in a strongly typed value (an int)
        // means we don't have to interpret the passed data on the other side,
        //  there's no risk of us messing it up because it's now strongly typed
        findNavController().navigate(action)
    }

    override fun onSaveClick(favourite: FavouriteEntity, cocktail: Cocktail, isFavourite: Boolean) {
        viewModel.favourites.observe(viewLifecycleOwner, Observer {
            with(it){
                // If favourite already exists in the list of favourites
                if (it.contains(favourite)) {
                    // remove favourite - still passing the entity but ultimately only using its ID
                    viewModel.removeFavourite(
                        favourite
                    )
                    adapter.notifyDataSetChanged();
                }
                else{
                    // The passed favourite is null (doesn't exist in the list of favourites held by the adapter, so was never initialised)
                    viewModel.saveFavourite(
                        FavouriteEntity(
                            cocktail.idDrink,
                            cocktail.strInstructions
                        )
                    )
                    adapter.notifyDataSetChanged();
                }
            }
        })


    }

}