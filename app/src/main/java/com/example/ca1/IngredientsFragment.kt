package com.example.ca1

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ca1.databinding.IngredientsFragmentBinding
import com.example.ca1.databinding.ViewFragmentBinding

class IngredientsFragment : Fragment() {

    companion object {
        fun newInstance() = IngredientsFragment()
    }
    private val args: IngredientsFragmentArgs by navArgs()

    private lateinit var viewModel: IngredientsViewModel
    private lateinit var binding: IngredientsFragmentBinding
    private lateinit var spinner: ProgressBar

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

        binding = IngredientsFragmentBinding.inflate(inflater, container, false);
        spinner = binding.progressBar2
        spinner.visibility = View.VISIBLE

        if(args.ingredientDescription != null && args.ingredientName!= null){
            binding.ingredientTitle.setText("${args.ingredientName}")

            binding.ingredientDescription.setText("${args.ingredientDescription}")

            spinner.visibility = View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveAndReturn()
                }
            }
        )

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
        viewModel = ViewModelProvider(this).get(IngredientsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}