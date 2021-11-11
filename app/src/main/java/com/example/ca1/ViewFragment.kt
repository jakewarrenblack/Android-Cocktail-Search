package com.example.ca1

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
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
        // initialise the binding
        binding = ViewFragmentBinding.inflate(inflater, container, false);
        // use this binding to update the TextView
        // set the text of the cocktail's TextView
        // a string literal like in JavaScript
        binding.cocktailText.setText("You selected cocktail number ${args.cocktailId}")
        // this file uses the view_fragment.xml as its layout file

        // we've already inflated the layout, so we'll just return the binding.root instead of returning the inflated layout
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewViewModel::class.java)

    }

}