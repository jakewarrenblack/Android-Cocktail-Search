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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ca1.data.FavouriteEntity
import com.example.ca1.databinding.ViewFragmentBinding
import kotlinx.coroutines.processNextEventInCurrentThread
import org.json.JSONArray
import org.json.JSONObject

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

        private lateinit var mainViewModel: MainViewModel

        private lateinit var responseJson: String

        private lateinit var adapter: IngredientsListAdapter

        private lateinit var spinner: ProgressBar

        private lateinit var myJson: String

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {


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
            spinner = binding.progressBar2

            spinner.visibility = View.VISIBLE;

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
            binding.cocktailText.setText("${args.cocktailName}")

            binding.cocktailInstructions.setText("${args.cocktailInstructions}")

            Glide.with(binding.root).load(args.cocktailImage).centerCrop()
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

            viewViewModel = ViewModelProvider(this).get(ViewViewModel::class.java)
            mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
            mainViewModel.getFullJson(args.cocktailId)




            binding.favouriteButton.setOnClickListener {
                saveFavourite();
            }


            // I need to get information back from this coroutine
            mainViewModel.currentFavourite.observe(viewLifecycleOwner, Observer {
                // If no existing cocktail is returned from the local storage DB
                if (mainViewModel.currentFavourite.value == null) {
                    binding.favouriteButton.text = "Not saved"
                    //binding.hasBeenFavouritedIndicator.text = "Not saved";
                } else {
                    binding.favouriteButton.text = "Saved!"
                    //binding.hasBeenFavouritedIndicator.text = "Saved!"
                }
            })

            mainViewModel.json.observe(viewLifecycleOwner, Observer {
                with(it) {
                    responseJson = it
                    var jsonArray = JSONObject(responseJson)
                    val drink: JSONObject
                    val drinksObject: JSONArray = jsonArray.getJSONArray("drinks")

                    // just get first drink, response is single
                    val singleDrink = drinksObject.getJSONObject(0)
                    // We loop through and fill this list
                    var ingredients = mutableMapOf<String, String>()
                    var i: Int = 0
                    // There are **always** 15 ingredients and corresponding measures
                    while (i < 15) {
                        i++
                        with(it) {
                            if (singleDrink.optString("strIngredient$i") != "null") {
                                if (it.toString().contains("strIngredient")) {
                                    val ingredient = singleDrink.optString("strIngredient$i")

                                    val measure = singleDrink.optString("strMeasure$i")
                                    // optString returns null if nothing there
                                    ingredients.put(measure, ingredient)
                                }
                            }
                        }
                    }

                    if (ingredients.isNotEmpty()) {
                        adapter = IngredientsListAdapter(ingredients, this@ViewFragment)
                        binding.ingredientsRecyclerView.adapter = adapter
                        binding.ingredientsRecyclerView.layoutManager =
                            LinearLayoutManager(activity)

                        spinner.visibility = View.GONE;
                    }

                    // assign this list to the cocktail now...
                    // cocktailItems!![j].ingredients = ingredients
                }
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
            findNavController().navigateUp()
            return true
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel = ViewModelProvider(this).get(ViewViewModel::class.java)
            // tell the viewModel to get access the local database to see if there are favourite comments for the current plant
            mainViewModel.getFavourite(args.cocktailId)

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
                        args.cocktailId,
                        args.cocktailName,
                        args.cocktailInstructions,
                        args.cocktailImage
                    )
                )
            } else {
                Log.i("Favourite", "Cocktail does not already exist, saving")
                // If this cocktailId does not already correspond with an existing favourite
                mainViewModel.saveFavourite(
                    FavouriteEntity(
                        args.cocktailId,
                        args.cocktailName,
                        args.cocktailInstructions,
                        args.cocktailImage

                    )
                )
            }

            //})
        }
    }
