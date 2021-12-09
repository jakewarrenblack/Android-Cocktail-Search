package com.example.ca1

import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ca1.IngredientsFragment.Companion.newInstance
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.databinding.FavouritesFragmentBinding
import com.example.ca1.databinding.ViewFragmentBinding
import com.example.ca1.model.Ingredient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.processNextEventInCurrentThread
import org.json.JSONArray
import org.json.JSONObject
import android.os.Build
import androidx.core.os.bundleOf


import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.Navigation

import androidx.navigation.NavController
import com.example.ca1.api.RetrofitInstance
import com.example.ca1.data.MergedData
import com.example.ca1.model.Cocktail

class ViewFragment : Fragment(),

    IngredientsListAdapter.ListItemListener
    {
        private lateinit var viewModel: ViewViewModel
        // 'by' operator allows for lazy evaluation
        private val args: ViewFragmentArgs by navArgs()
        // again we use the ViewBinding library here
        private lateinit var binding: ViewFragmentBinding

        // I want to observe the result of the getFavourites function in here, need a reference to it
        private lateinit var viewViewModel: ViewViewModel

        private lateinit var ingredientsForLooping: MutableMap<String, String>

        private lateinit var favouritesViewModel: FavouritesViewModel

        private lateinit var favouritesFragmentBinding: FavouritesFragmentBinding

        private lateinit var mainViewModel: MainViewModel

        private lateinit var responseJson: String

        private lateinit var adapter: IngredientsListAdapter

        private lateinit var spinner: ProgressBar

        private lateinit var myJson: String

        private lateinit var ingredients: MutableMap<String, String>

        private var ingredientsWithDescriptions: List<Ingredient?>? = mutableListOf()

        var currentFavouriteItem: FavouriteEntity? = null
        var ingredientItems: List<Ingredient?>? = null


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            viewViewModel = ViewModelProvider(this).get(ViewViewModel::class.java)

            val cocktail: Cocktail = args.cocktail

            // Iterators need non-nullable values
            ingredients = args.cocktail.ingredients!!
            val liveData = viewViewModel.fetchData(ingredients)

            // get a reference to the activity which owns this fragment
            (activity as AppCompatActivity).supportActionBar?.let {
                it.setHomeButtonEnabled(true)
                it.setDisplayShowHomeEnabled(true)
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.ic_check)
            }

            setHasOptionsMenu(true)

            // initialise the binding
            binding = ViewFragmentBinding.inflate(inflater, container, false);
            favouritesFragmentBinding = FavouritesFragmentBinding.inflate(inflater, container, false)
            //spinner = binding.progressBar2

            //spinner.visibility = View.VISIBLE;

            with(binding.ingredientsRecyclerView) {
                // height of each row same regardless of contents
                setHasFixedSize(true)
                // this is from the recyclerView's widget package
                val divider = DividerItemDecoration(
                    context, LinearLayoutManager(context).orientation
                )
            }

            // use this binding to update the TextView
            // set the text of the cocktail's TextView
            // a string literal like in JavaScript
            binding.cocktailText.setText("${args.cocktail.strDrink}")

            binding.cocktailInstructions.setText("${args.cocktail.strInstructions}")

            Glide.with(binding.root).load(args.cocktail.strDrinkThumb).centerCrop()
                .into(binding.cocktailImage)
            // this file uses the view_fragment.xml as its layout file

            // if the user runs a back gesture either through an actual button OR a gesture,
            // handle it by calling the custom saveAndReturn() function which will result
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        saveAndReturn()
                    }
                }
            )

            mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

            favouritesViewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)

            //mainViewModel.getFullJson(args.cocktailId)

            binding.favouriteButton.setOnClickListener {
                saveFavourite();
            }

            // When we've finished populating the list of ingredient details in the viewviewmodel
                liveData.observe(viewLifecycleOwner,
                    { it ->
                        when (it) {
                            is MergedData.CurrentFavouriteData -> currentFavouriteItem =
                                it.currentFavouriteItem
                            is MergedData.IngredientsData -> ingredientItems = it.ingredientItems
                        }

                        if (currentFavouriteItem == null) {
                            binding.favouriteButton.text = "Not saved"
                        } else {
                            binding.favouriteButton.text = "Saved!"
                        }

                        if (ingredientItems != null) {
                            ingredientsWithDescriptions = ingredientItems
                        }


                        adapter = args.cocktail.ingredients?.let {
                            ingredientsWithDescriptions?.let { it1 ->
                                IngredientsListAdapter(
                                    it,
                                    it1, this@ViewFragment
                                )
                            }
                        }!!
                        binding.ingredientsRecyclerView.adapter = adapter
                        binding.ingredientsRecyclerView.layoutManager =
                            LinearLayoutManager(activity)
//
//
//                    if(it == null){
//                        spinner.visibility = View.VISIBLE;
//                    } else{
//                        spinner.visibility = View.GONE;
//                    }
                    })


            val myCustomFont : Typeface? = getActivity()?.let { ResourcesCompat.getFont(it, R.font.lobster_regular) }
            binding.cocktailText.typeface = myCustomFont

            // we've already inflated the layout, so we'll just return the binding.root instead of returning the inflated layout
            return binding.root
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                android.R.id.home -> saveAndReturn()
                else -> super.onOptionsItemSelected(item)
            }
        }

        private fun saveAndReturn(): Boolean {
            // Instead of just navigating up, I'm telling the nav controller to create a new reference to the
            // favouritesFragment and navigate to it, ensures the view is re-inflated and viewmodel made again,
            // so if we've unsaved a favourite from here, the adapter will get fresh data and change accordingly

            // We also have to manually apply the animations, if we didn't they wouldn't happen

            // We *were* just using navigation.navigateUp, but in this case it seemed like the FavouritesFragment was just being *resumed* rather than recreated,
            // which is what we want, to make sure the list is updated if it's changed from the ViewFragment (in here)

            // Had to add argument field to pass the name of the fragment
            // Without this check, we'd navigate back to the favourites fragment every time, even when only viewing a cocktail on the MainFragment
            // This problem arose because MainFragment and FavouritesFragment both link through to the same ViewFragment
            if(args.fragmentname.toLowerCase() == "mainfragment") {
                findNavController().navigateUp()
                return true
            }
            else{
                val navBuilder = NavOptions.Builder()
                navBuilder
                    .setEnterAnim(android.R.anim.slide_in_left)
                    .setExitAnim(android.R.anim.slide_out_right)
                    .setPopEnterAnim(android.R.anim.slide_in_left)
                    .setPopExitAnim(android.R.anim.slide_out_right)
                    .setPopUpTo(R.id.favouritesFragment, true)

                NavHostFragment.findNavController(this@ViewFragment)
                    .navigate(R.id.favouritesFragment, arguments, navBuilder.build())

                return true
            }
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel = ViewModelProvider(this).get(ViewViewModel::class.java)
            // tell the viewModel to get access the local database to see if there are favourite comments for the current plant
            mainViewModel.getFavourite(args.cocktail.idDrink)

        }

        // Trying to implement functionality for the 'favourite' button and Room DB
        private fun saveFavourite() {
            Log.i("Favourite", "Clicked save favourite!")
            //viewViewModel.currentFavourite.observe(viewLifecycleOwner, Observer{
            // Find out if this cocktail exists in our database by observing the value from the coroutine in ViewViewModel
            if (mainViewModel.currentFavourite.value != null) {
                Log.i("Favourite", "Cocktail already exists, unsaving")
                // remove favourite - still passing the entity but ultimately only using its ID
                mainViewModel.removeFavourite(
                    FavouriteEntity(
                        args.cocktail.idDrink,
                        args.cocktail.strDrink,
                        args.cocktail.strInstructions,
                        args.cocktail.strDrinkThumb
                    )
                )

            } else {
                Log.i("Favourite", "Cocktail does not already exist, saving")
                // If this cocktailId does not already correspond with an existing favourite
                mainViewModel.saveFavourite(
                    FavouriteEntity(
                        args.cocktail.idDrink,
                        args.cocktail.strDrink,
                        args.cocktail.strInstructions,
                        args.cocktail.strDrinkThumb

                    )
                )
            }
        }

        fun getIngredientDetails(ingredientName: String){
            viewViewModel.getIngredientDetailsByName(ingredientName)
        }

        fun navigateToNextPage(ingredientName: String, ingredientDescription: String){
            val action =
                ViewFragmentDirections.actionViewFragmentToIngredientsFragment(
                    ingredientName,
                    ingredientDescription
                )
            findNavController().navigate(action)
        }

//        fun checkHasDescription(ingredientName: String){
//                //getIngredientDetails(ingredientName)
//
//                viewViewModel.ingredientDetails?.observe(viewLifecycleOwner, Observer{
//                    with(it){
//                        // Get the name of the ingredient
//                        if(it!= null) {
//                            if(it[0].strDescription!=null) {
//                                if (it[0].strDescription?.isNotEmpty() == true) {
//                                    // Make sure returned details match the the name of the ingredient we've clicked on
//                                    if (ingredientName.equals(it[0].strIngredient, ignoreCase = true)) {
//                                        it[0].strIngredient?.let { it1 ->
//                                            ingredientsWithDescriptions.add(
//                                                it1
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                })
//        }



        override fun onItemClicked(ingredientName: String) {
            getIngredientDetails(ingredientName)

            viewViewModel.ingredientDetails?.observe(viewLifecycleOwner, Observer{
                with(it){
                    // Get the name of the ingredient
                    if(it!= null) {
                        if(it[0].strDescription!=null) {
                            if (it[0].strDescription?.isNotEmpty() == true) {
                                // Make sure returned details match the the name of the ingredient we've clicked on
                                if (ingredientName.equals(it[0].strIngredient, ignoreCase = true)) {
                                    it[0].strDescription?.let { it1 ->
                                        navigateToNextPage(ingredientName,
                                            it1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }
