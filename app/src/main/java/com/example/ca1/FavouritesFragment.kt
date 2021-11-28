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

        with(binding.recyclerView){
            setHasFixedSize(true)
            val divider = DividerItemDecoration(
                context, LinearLayoutManager(context).orientation
            )
        }


        viewModel.favourites.observe(viewLifecycleOwner, Observer{
            if(viewModel.favourites.value != null){
                this.favouriteItems = it
                spinner.visibility = View.GONE;
                if(favouriteItems != null){
                    viewModel.getCocktails(favouriteItems)

                    adapter = FavouritesListAdapter(favouriteItems, this@FavouritesFragment)
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(activity)

                }

            }
        })

        return binding.root
    }

    override fun onItemClick(cocktailId: Int, cocktailInstructions: String, cocktailName: String) {
        Log.i(TAG, "onItemClick: received cocktail id $cocktailId")
        val action = MainFragmentDirections.actionViewCocktail(cocktailId, cocktailInstructions, cocktailName)
        findNavController().navigate(action)
    }

    override fun onSaveClick(cocktail: Cocktail, isFavourite: Boolean, adapterFavouriteId: Int?, position: Int) {

    }
}