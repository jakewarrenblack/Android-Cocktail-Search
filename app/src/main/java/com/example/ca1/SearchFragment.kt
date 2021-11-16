package com.example.ca1

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.example.ca1.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: SearchFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        binding = SearchFragmentBinding.inflate(inflater, container, false);

        val searchView = binding.searchView;

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
//                if (bestCities.contains(p0)) {
//                    adapter.filter.filter(p0)
//                } else {
//                    Toast.makeText(this@MainActivity, "No match found", Toast.LENGTH_SHORT).show()
//                }
                Log.i("Search (enter pressed):", "$p0")

                // I think we want to navigate through to the main fragment,
                // and have a way of seeing what was passed?

                val action = SearchFragmentDirections.actionViewMain(p0.toString())
                // get a reference to the navigation host, passing in a strongly typed value (an int)
                // means we don't have to interpret the passed data on the other side,
                //  there's no risk of us messing it up because it's now strongly typed

//                viewModel.searchQuery = p0.toString()

                viewModel.getCocktails(p0.toString())

                findNavController().navigate(action)


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i("Search text:", "$newText")
                return false
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

    }

}