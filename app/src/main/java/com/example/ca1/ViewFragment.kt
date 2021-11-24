package com.example.ca1

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.databinding.ViewFragmentBinding
import kotlinx.coroutines.processNextEventInCurrentThread

class ViewFragment : Fragment() {

    private lateinit var viewModel: ViewViewModel
    // 'by' operator allows for lazy evaluation
    private val args: ViewFragmentArgs by navArgs()
    // again we use the ViewBinding library here
    private lateinit var binding: ViewFragmentBinding

    // I want to observe the result of the getFavourites function in here, need a reference to it
    private lateinit var viewViewModel: ViewViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // get a reference to the activity which owns this fragment
        (activity as AppCompatActivity).supportActionBar?.let{
            it.setHomeButtonEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_check)
        }

        setHasOptionsMenu(true)


        // initialise the binding
        binding = ViewFragmentBinding.inflate(inflater, container, false);
        // use this binding to update the TextView
        // set the text of the cocktail's TextView
        // a string literal like in JavaScript
        binding.cocktailText.setText("${args.cocktailName}")

        binding.cocktailInstructions.setText("${args.cocktailInstructions}")
        // this file uses the view_fragment.xml as its layout file

        // if the user runs a back gesture either through an actual button OR a gesture,
        // handle it by calling the custom saveAndReturn() function which will result
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    saveAndReturn()
                }
            }
        )

        viewViewModel = ViewModelProvider(this).get(ViewViewModel::class.java)



        binding.favouriteButton.setOnClickListener {
            saveFavourite();
        }

        // I know I need to observe this data, still a bit confused on how exactly to do it
        // I need to get information back from this coroutine
        viewViewModel.currentFavourite.observe(viewLifecycleOwner, Observer{
            // If no existing cocktail is returned from the local storage DB
            if(viewViewModel.currentFavourite.value == null){
                binding.hasBeenFavouritedIndicator.text = "Not saved";
            }
            else{
                binding.hasBeenFavouritedIndicator.text = "Saved!"
            }
        })

            // we've already inflated the layout, so we'll just return the binding.root instead of returning the inflated layout
            return binding.root
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        return when (item.itemId){
            android.R.id.home -> saveAndReturn()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveAndReturn(): Boolean {
        findNavController().navigateUp()
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewViewModel::class.java)
        // tell the viewModel to get access the local database to see if there are favourite comments for the current plant
        viewModel.getFavourite(args.cocktailId)

    }

    // Trying to implement functionality for the 'favourite' button and Room DB
    private fun saveFavourite(){
        Log.i("Favourite", "Clicked save favourite!")
        //viewViewModel.currentFavourite.observe(viewLifecycleOwner, Observer{
            // Find out if this cocktail exists in our database by observing the value from the coroutine in ViewViewModel
            if(viewViewModel.currentFavourite.value != null){
                Log.i("Favourite", "Cocktail already exists, unsaving")
                // remove favourite - still passing the entity but ultimately only using its ID
                viewModel.removeFavourite(
                    FavouriteEntity(
                        args.cocktailId,
                        binding.cocktailInstructions.toString()
                    )
                )
            }
            else{
                Log.i("Favourite", "Cocktail does not already exist, saving")
                // If this cocktailId does not already correspond with an existing favourite
                viewModel.saveFavourite(
                    FavouriteEntity(
                        args.cocktailId,
                        binding.cocktailInstructions.toString()
                    )
                )
            }

        //})
    }
}