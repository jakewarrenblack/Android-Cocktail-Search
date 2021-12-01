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
    private lateinit var viewModel: FavouritesViewModel
    private lateinit var binding: FavouritesFragmentBinding
    private lateinit var adapter: FavouritesListAdapter
    private lateinit var spinner: ProgressBar
    var cocktailItems: List<Cocktail>? = null
    var favouriteItems: MutableList<FavouriteEntity?>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

    override fun onItemClick(cocktailId: Int, cocktailInstructions: String, cocktailName: String, cocktailImage: String) {
        Log.i(TAG, "onItemClick: received cocktail id $cocktailId")
        val action = FavouritesFragmentDirections.actionViewCocktail(cocktailId, cocktailInstructions, cocktailName, cocktailImage)
        findNavController().navigate(action)
    }

    override fun onSaveClick(favourite: FavouriteEntity, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int) {
        // In the MainFragment we have an if statement to check if the cocktail is an existing favourite, but in this case we know it is, because we're looking at a list of FavouriteEntities
            Log.i("FavouriteExistence", "Rremoving favourite: ${favourite.id} / adapterfavourite: $adapterFavouriteId")
            favouriteItems?.remove(favourite)
            viewModel.removeFavourite(favourite)
            binding.favouritesRecyclerView.removeViewAt(position)
            //adapter = FavouritesListAdapter(favouriteItems, this@FavouritesFragment)

            adapter.notifyItemRemoved(position);

            // Update the UI if we've just the unsaved the only favourite
            if(favouriteItems?.isEmpty() == true){
                binding.noFavouritesSaved.visibility = View.VISIBLE
            }
            //adapter.notifyItemRangeChanged(position, favouriteItems?.size!!);
            //adapter.notifyDataSetChanged()

    }
}