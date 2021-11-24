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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.databinding.ViewFragmentBinding

class ViewFragment : Fragment() {

    private lateinit var viewModel: ViewViewModel
    // 'by' operator allows for lazy evaluation
    private val args: ViewFragmentArgs by navArgs()
    // again we use the ViewBinding library here
    private lateinit var binding: ViewFragmentBinding

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



        binding.favouriteButton.setOnClickListener {
            saveFavourite();
        }

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
        Log.i("Favourite", "CLicked save favourite!")
        viewModel.saveFavourite(FavouriteEntity(args.cocktailId, binding.cocktailInstructions.toString()))
    }

}