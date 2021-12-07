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
import com.example.ca1.databinding.FavouritesFragmentBinding
import com.example.ca1.databinding.MainFragmentBinding
import com.example.ca1.model.Cocktail

class FavouritesFragment : Fragment(),
    FavouritesListAdapter.ListItemListener{
    companion object {
        fun newInstance() = FavouritesFragment()
    }

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var binding: FavouritesFragmentBinding
    private lateinit var adapter: FavouritesListAdapter
    private lateinit var spinner: ProgressBar
    var cocktailItems: List<Cocktail>? = null
    var favouriteItems: MutableList<FavouriteEntity?>? = null

    override fun onResume() {
        super.onResume()
        favouriteItems = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /*
        "When an activity is popped off the stack by navigating back,
        it doesn't retain any of its views or state.
        The activity object and all its views and state are destroyed."

        This presents a problem for us when trying to ensure an item is removed
        from the recyclerview when the user unsaves it from the viewfragment side.

        We pass the cocktail position through when an item is clicked so the viewfragment has a reference to it,
        and then if the user clicks unsave from the viewfragment, we pass this integer back up the stack,
        and get our favouritesfragment to retrieve it on load,

        now we can make sure this item is removed from the newly created adapter

        // TODO:
        WE ALSO NEED TO FIGURE OUT PASSING A FAVOURITE OBJECT,
        THE POSITION WILL ONLY SERVE TO REMOVE IT FROM THE UI, WITHOUT ACTUALLY UPDATING THE DB
         */


        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding = FavouritesFragmentBinding.inflate(inflater, container, false)
        spinner = binding.progressBar1

        // It's important to obtain an instance of the viewModel during view creation
        viewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)

        //val liveData = viewModel.fetchData()

        with(binding.favouritesRecyclerView){
            setHasFixedSize(true)
            val divider = DividerItemDecoration(
                context, LinearLayoutManager(context).orientation
            )
        }


        viewModel.favourites.observe(viewLifecycleOwner, Observer{
            if(viewModel.favourites.value != null){
                if (it != null) {
                    if(it.isNotEmpty()) {
                        this.favouriteItems = it
                        spinner.visibility = View.GONE;
                        if (favouriteItems != null) {
                            viewModel.getCocktails(favouriteItems)

                            adapter = FavouritesListAdapter(favouriteItems, this@FavouritesFragment)
                            binding.favouritesRecyclerView.adapter = adapter
                            binding.favouritesRecyclerView.layoutManager = LinearLayoutManager(activity)

                        }
                    }
                    else{
                        spinner.visibility = View.GONE
                        binding.noFavouritesSaved.visibility = View.VISIBLE
                    }
                }

            }
        })

        return binding.root
    }

    // Position is used to get a reference to where in the list this specific item is, that means we can update the UI even from the single view page
    override fun onItemClick(cocktail: Cocktail, fragmentName: String) {
        //Log.i(TAG, "onItemClick: received cocktail id $cocktailId")



        val action = FavouritesFragmentDirections.actionViewCocktail(cocktail, fragmentName)
        findNavController().navigate(action)
    }

    override fun onSaveClick(favourite: FavouriteEntity, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int) {
        // In the MainFragment we have an if statement to check if the cocktail is an existing favourite, but in this case we know it is, because we're looking at a list of FavouriteEntities
            Log.i("FavouriteExistence", "Removing favourite: ${favourite.id} ${ favourite.strDrink} / adapterfavourite: $adapterFavouriteId")

            // The order of these is important, if we removed the favourite and updated the adapter's data, the view at this position would be removed, and the app would crash on a null pointer exception
            binding.favouritesRecyclerView.removeViewAt(position)

            favouriteItems?.remove(favourite)
            viewModel.removeFavourite(favourite)

            adapter.notifyItemRemoved(position);

            // Update the UI if we've just the unsaved the only favourite
            if(favouriteItems?.isEmpty() == true){
                binding.noFavouritesSaved.visibility = View.VISIBLE
            }
    }
}